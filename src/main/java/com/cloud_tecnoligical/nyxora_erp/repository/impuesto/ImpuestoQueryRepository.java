package com.cloud_tecnoligical.nyxora_erp.repository.impuesto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.ImpuestoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.ImpuestoTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ImpuestoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "tipo", "created_at");

    public ImpuestoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long vigenciaId, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM impuesto
                WHERE codigo=:c AND vigencia_id=:v AND empresa_id=:e AND deleted_at IS NULL
                """)
            .bind("c", codigo).bind("v", vigenciaId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long vigenciaId, Long id, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM impuesto
                WHERE codigo=:c AND vigencia_id=:v AND empresa_id=:e AND id<>:id AND deleted_at IS NULL
                """)
            .bind("c", codigo).bind("v", vigenciaId).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<ImpuestoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT i.id AS "id", i.codigo AS "codigo", i.nombre AS "nombre", i.tipo AS "tipo",
                       i.causacion AS "causacion", i.base_gravable AS "baseGravable",
                       i.periodicidad AS "periodicidad", i.aplica_aiu AS "aplicaAiu",
                       i.retencion_nomina AS "retencionNomina", i.tarifa AS "tarifa",
                       i.vigencia_id AS "vigenciaId", i.cuenta_compra_id AS "cuentaCompraId",
                       i.cuenta_venta_id AS "cuentaVentaId", i.activo AS "active", i.created_at AS "createdAt"
                FROM impuesto i WHERE i.id=:id AND i.empresa_id=:e AND i.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ImpuestoResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ImpuestoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT i.id AS "id", i.codigo AS "codigo", i.nombre AS "nombre", i.tipo AS "tipo",
                       i.tarifa AS "tarifa", i.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM impuesto i WHERE i.empresa_id=:e AND i.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(i.nombre) LIKE LOWER(:search) OR LOWER(i.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY i.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<ImpuestoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ImpuestoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
