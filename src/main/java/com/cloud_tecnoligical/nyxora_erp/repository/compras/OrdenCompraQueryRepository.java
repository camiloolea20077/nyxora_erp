package com.cloud_tecnoligical.nyxora_erp.repository.compras;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class OrdenCompraQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "numero", "estado", "created_at");

    public OrdenCompraQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<OrdenCompraResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT o.id AS "id", o.sede_id AS "sedeId", o.vigencia_id AS "vigenciaId",
                       o.tipo_documento_id AS "tipoDocumentoId", o.numero AS "numero", o.proveedor_id AS "proveedorId",
                       o.bodega_id AS "bodegaId", o.centro_costo_id AS "centroCostoId",
                       o.condicion_pago_id AS "condicionPagoId", o.fecha AS "fecha", o.fecha_entrega AS "fechaEntrega",
                       o.observaciones AS "observaciones", o.estado AS "estado", o.subtotal AS "subtotal",
                       o.descuento AS "descuento", o.impuestos AS "impuestos", o.total AS "total",
                       o.activo AS "active", o.created_at AS "createdAt"
                FROM orden_compra o WHERE o.id=:id AND o.empresa_id=:e AND o.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, OrdenCompraResponseDto.class));
    }

    public Mono<List<OrdenCompraLineaResponseDto>> listLineas(Long ordenCompraId) {
        return db.sql("""
                SELECT id AS "id", orden_compra_id AS "ordenCompraId", producto_id AS "productoId",
                       producto_variante_id AS "productoVarianteId", descripcion AS "descripcion",
                       cantidad AS "cantidad", unidad_medida_id AS "unidadMedidaId", valor_unitario AS "valorUnitario",
                       descuento_porcentaje AS "descuentoPorcentaje", descuento_valor AS "descuentoValor",
                       impuesto_id AS "impuestoId", impuesto_porcentaje AS "impuestoPorcentaje",
                       impuesto_valor AS "impuestoValor", subtotal AS "subtotal", total AS "total",
                       cantidad_recibida AS "cantidadRecibida", cantidad_pendiente AS "cantidadPendiente",
                       centro_costo_id AS "centroCostoId"
                FROM orden_compra_linea WHERE orden_compra_id=:o AND deleted_at IS NULL ORDER BY id
                """)
            .bind("o", ordenCompraId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, OrdenCompraLineaResponseDto.class))
            .collectList();
    }

    public Mono<Long> borrarLineas(Long ordenCompraId) {
        return db.sql("DELETE FROM orden_compra_linea WHERE orden_compra_id=:o").bind("o", ordenCompraId).fetch().rowsUpdated();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<OrdenCompraTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT o.id AS "id", o.numero AS "numero", o.proveedor_id AS "proveedorId", o.fecha AS "fecha",
                       o.estado AS "estado", o.total AS "total", o.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM orden_compra o WHERE o.empresa_id=:e AND o.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (o.numero LIKE :search OR LOWER(o.observaciones) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY o.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<OrdenCompraTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, OrdenCompraTableDto.class))
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

    /** Suma de lo pendiente por recibir (cantidad − cantidad_recibida) de las líneas de la orden. */
    public Mono<java.math.BigDecimal> pendienteTotal(Long ordenCompraId) {
        return db.sql("""
                SELECT COALESCE(SUM(cantidad - cantidad_recibida), 0) AS pend
                FROM orden_compra_linea WHERE orden_compra_id=:o AND deleted_at IS NULL
                """)
            .bind("o", ordenCompraId)
            .map(row -> {
                Object v = row.get("pend");
                return v instanceof java.math.BigDecimal bd ? bd : new java.math.BigDecimal(v.toString());
            }).one();
    }
}
