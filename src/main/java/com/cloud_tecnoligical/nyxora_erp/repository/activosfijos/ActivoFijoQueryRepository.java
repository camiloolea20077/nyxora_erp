package com.cloud_tecnoligical.nyxora_erp.repository.activosfijos;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoPolizaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoResponsableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ActivoFijoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "valor_compra", "created_at");

    public ActivoFijoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM activo_fijo WHERE codigo=:c AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM activo_fijo WHERE codigo=:c AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> terceroExists(Long terceroId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM tercero WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", terceroId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> polizaExistsInTenant(Long polizaId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM poliza_seguro WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", polizaId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Long> responsableVigenteId(Long activoFijoId, Long terceroId) {
        return db.sql("SELECT id FROM activo_fijo_responsable WHERE activo_fijo_id=:a AND tercero_id=:t AND deleted_at IS NULL LIMIT 1")
            .bind("a", activoFijoId).bind("t", terceroId)
            .map(row -> ((Number) row.get("id")).longValue()).one();
    }

    public Mono<Long> polizaVigenteId(Long activoFijoId, Long polizaId) {
        return db.sql("SELECT id FROM activo_fijo_poliza WHERE activo_fijo_id=:a AND poliza_seguro_id=:p AND deleted_at IS NULL LIMIT 1")
            .bind("a", activoFijoId).bind("p", polizaId)
            .map(row -> ((Number) row.get("id")).longValue()).one();
    }

    public Mono<ActivoFijoResponseDto> findHeaderById(Long id, Long empresaId) {
        return db.sql("""
                SELECT af.id AS "id", af.producto_id AS "productoId", af.codigo AS "codigo",
                       af.codigo_unspsc AS "codigoUnspsc", af.codigo_barra AS "codigoBarra", af.nombre AS "nombre",
                       af.descripcion AS "descripcion", af.marca_id AS "marcaId", af.unidad_mayor_id AS "unidadMayorId",
                       af.numero_serie AS "numeroSerie", af.modelo AS "modelo", af.bodega_id AS "bodegaId",
                       af.centro_costo_id AS "centroCostoId", af.proveedor_id AS "proveedorId",
                       COALESCE(pr.razon_social, pr.primer_nombre) AS "proveedorNombre",
                       af.numero_factura AS "numeroFactura", af.fecha_factura AS "fechaFactura",
                       af.valor_compra AS "valorCompra", af.valor_salvamento AS "valorSalvamento",
                       af.porcentaje_salvamento AS "porcentajeSalvamento", af.metodo_depreciacion AS "metodoDepreciacion",
                       af.tipo_depreciacion AS "tipoDepreciacion", af.valor_depreciacion AS "valorDepreciacion",
                       af.deterioro AS "deterioro", af.valor_actual AS "valorActual", af.avaluo AS "avaluo",
                       af.vida_util AS "vidaUtil", af.meses_depreciados AS "mesesDepreciados",
                       af.capitalizado AS "capitalizado", af.estado_activo AS "estadoActivo",
                       af.fecha_salida_servicio AS "fechaSalidaServicio", af.activo AS "active", af.created_at AS "createdAt"
                FROM activo_fijo af
                LEFT JOIN tercero pr ON pr.id = af.proveedor_id
                WHERE af.id=:id AND af.empresa_id=:e AND af.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ActivoFijoResponseDto.class));
    }

    public Mono<List<ActivoFijoResponsableDto>> listResponsables(Long activoFijoId) {
        return db.sql("""
                SELECT r.id AS "id", r.tercero_id AS "terceroId",
                       COALESCE(te.razon_social, te.primer_nombre) AS "terceroNombre", r.activo AS "active"
                FROM activo_fijo_responsable r
                LEFT JOIN tercero te ON te.id = r.tercero_id
                WHERE r.activo_fijo_id=:a AND r.deleted_at IS NULL ORDER BY r.id
                """)
            .bind("a", activoFijoId)
            .fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, ActivoFijoResponsableDto.class))
            .collectList();
    }

    public Mono<List<ActivoFijoPolizaDto>> listPolizas(Long activoFijoId) {
        return db.sql("""
                SELECT ap.id AS "id", ap.poliza_seguro_id AS "polizaSeguroId", p.numero AS "numero",
                       p.tipo AS "tipo", p.valor_asegurado AS "valorAsegurado"
                FROM activo_fijo_poliza ap
                JOIN poliza_seguro p ON p.id = ap.poliza_seguro_id
                WHERE ap.activo_fijo_id=:a AND ap.deleted_at IS NULL ORDER BY ap.id
                """)
            .bind("a", activoFijoId)
            .fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, ActivoFijoPolizaDto.class))
            .collectList();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ActivoFijoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT af.id AS "id", af.codigo AS "codigo", af.nombre AS "nombre", af.numero_serie AS "numeroSerie",
                       af.valor_compra AS "valorCompra", af.valor_actual AS "valorActual",
                       af.estado_activo AS "estadoActivo", af.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM activo_fijo af WHERE af.empresa_id=:e AND af.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(af.nombre) LIKE LOWER(:search) OR LOWER(af.codigo) LIKE LOWER(:search) OR LOWER(af.numero_serie) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY af.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<ActivoFijoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ActivoFijoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
