package com.cloud_tecnoligical.nyxora_erp.repository.facturacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class FacturaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "numero", "estado", "created_at");

    public FacturaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<FacturaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT f.id AS "id", f.sede_id AS "sedeId", f.vigencia_id AS "vigenciaId",
                       f.tipo_documento_id AS "tipoDocumentoId", f.resolucion_dian_id AS "resolucionDianId",
                       f.numero AS "numero", f.cliente_id AS "clienteId", f.bodega_id AS "bodegaId",
                       f.centro_costo_id AS "centroCostoId", f.condicion_pago_id AS "condicionPagoId",
                       f.fecha AS "fecha", f.fecha_vencimiento AS "fechaVencimiento", f.observaciones AS "observaciones",
                       f.estado AS "estado", f.subtotal AS "subtotal", f.descuento AS "descuento",
                       f.impuestos AS "impuestos", f.total AS "total", f.activo AS "active", f.created_at AS "createdAt"
                FROM factura f WHERE f.id=:id AND f.empresa_id=:e AND f.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, FacturaResponseDto.class));
    }

    public Mono<List<FacturaLineaResponseDto>> listLineas(Long facturaId) {
        return db.sql("""
                SELECT id AS "id", factura_id AS "facturaId", producto_id AS "productoId",
                       producto_variante_id AS "productoVarianteId", descripcion AS "descripcion",
                       cantidad AS "cantidad", unidad_medida_id AS "unidadMedidaId", valor_unitario AS "valorUnitario",
                       descuento_porcentaje AS "descuentoPorcentaje", descuento_valor AS "descuentoValor",
                       subtotal AS "subtotal", impuesto_id AS "impuestoId", porcentaje_impuesto AS "porcentajeImpuesto",
                       valor_impuesto AS "valorImpuesto", discrimina_iva AS "discriminaIva", total AS "total",
                       bodega_id AS "bodegaId", lote_id AS "loteId", centro_costo_id AS "centroCostoId"
                FROM factura_linea WHERE factura_id=:f AND deleted_at IS NULL ORDER BY id
                """)
            .bind("f", facturaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, FacturaLineaResponseDto.class))
            .collectList();
    }

    public Mono<Long> borrarLineas(Long facturaId) {
        return db.sql("DELETE FROM factura_linea WHERE factura_id=:f").bind("f", facturaId).fetch().rowsUpdated();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<FacturaTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT f.id AS "id", f.numero AS "numero", f.cliente_id AS "clienteId", f.fecha AS "fecha",
                       f.estado AS "estado", f.total AS "total", f.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM factura f WHERE f.empresa_id=:e AND f.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (f.numero LIKE :search OR LOWER(f.observaciones) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY f.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<FacturaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, FacturaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    // ---------- validaciones ----------
    public Mono<Boolean> terceroExisteEnEmpresa(Long terceroId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM tercero WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", terceroId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Long> countProductosEnEmpresa(Set<Long> ids, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM producto WHERE id IN (:ids) AND empresa_id=:e AND deleted_at IS NULL")
            .bind("ids", ids).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one();
    }

    /** Costo promedio ponderado del producto en la bodega (COGS para la salida). 0 si no hay saldo. */
    public Mono<BigDecimal> costoPromedio(Long empresaId, Long bodegaId, Long productoId, Long varianteId) {
        StringBuilder sql = new StringBuilder("""
                SELECT COALESCE(SUM(valor_total) / NULLIF(SUM(cantidad), 0), 0) AS costo
                FROM saldo_inventario WHERE empresa_id=:e AND bodega_id=:b AND producto_id=:p AND cantidad > 0
                """);
        if (varianteId != null) {
            sql.append(" AND producto_variante_id=:v ");
        }
        var spec = db.sql(sql.toString()).bind("e", empresaId).bind("b", bodegaId).bind("p", productoId);
        if (varianteId != null) {
            spec = spec.bind("v", varianteId);
        }
        return spec.map(row -> {
            Object v = row.get("costo");
            return v == null ? BigDecimal.ZERO
                : (v instanceof BigDecimal bd ? bd : new BigDecimal(v.toString()));
        }).one();
    }

    /**
     * Reversa las salidas de inventario de la factura insertando movimientos 'entrada' compensatorios
     * (append-only, mismo costo_unitario → neutral para el costo promedio). Devuelve filas insertadas.
     * DEBE ejecutarse dentro de la transacción de anulación.
     */
    public Mono<Long> reversarMovimientosInventario(Long facturaId, Long empresaId, Long usuarioId, LocalDate fecha) {
        return db.sql("""
                INSERT INTO movimiento_inventario (empresa_id, bodega_id, ubicacion_id, producto_id,
                    producto_variante_id, lote_id, tipo, fecha, cantidad, costo_unitario, subtotal, total,
                    descripcion, origen_modulo, origen_id, usuario_creacion, created_at)
                SELECT empresa_id, bodega_id, ubicacion_id, producto_id, producto_variante_id, lote_id,
                    'entrada', :fecha, -cantidad, costo_unitario, subtotal, total,
                    'Reversa anulación factura #' || :f, 'facturacion', :f, :u, now()
                FROM movimiento_inventario
                WHERE origen_modulo='facturacion' AND origen_id=:f AND tipo='salida' AND empresa_id=:e
                """)
            .bind("fecha", fecha).bind("f", facturaId).bind("u", usuarioId).bind("e", empresaId)
            .fetch().rowsUpdated();
    }

    // ---------- factura electrónica ----------
    public Mono<FacturaDianResponseDto> findDianByFacturaId(Long facturaId) {
        return db.sql("""
                SELECT id AS "id", factura_id AS "facturaId", cufe AS "cufe", estado_dian AS "estadoDian",
                       fecha_acuse AS "fechaAcuse", comentario_acuse AS "comentarioAcuse", created_at AS "createdAt"
                FROM factura_dian WHERE factura_id=:f AND deleted_at IS NULL LIMIT 1
                """)
            .bind("f", facturaId).fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, FacturaDianResponseDto.class));
    }
}
