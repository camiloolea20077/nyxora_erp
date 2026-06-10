package com.cloud_tecnoligical.nyxora_erp.repository.organizacion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.ProyectoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.ProyectoTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ProyectoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "fecha_inicio", "created_at");

    public ProyectoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM proyecto WHERE codigo=:c AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM proyecto WHERE codigo=:c AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<ProyectoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT p.id AS "id", p.codigo AS "codigo", p.nombre AS "nombre", p.descripcion AS "descripcion",
                       p.programa_id AS "programaId", p.fecha_inicio AS "fechaInicio", p.fecha_final AS "fechaFinal",
                       p.activo AS "active", p.created_at AS "createdAt"
                FROM proyecto p WHERE p.id=:id AND p.empresa_id=:e AND p.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ProyectoResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ProyectoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT p.id AS "id", p.codigo AS "codigo", p.nombre AS "nombre",
                       p.fecha_inicio AS "fechaInicio", p.fecha_final AS "fechaFinal", p.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM proyecto p WHERE p.empresa_id=:e AND p.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(p.nombre) LIKE LOWER(:search) OR LOWER(p.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY p.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<ProyectoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ProyectoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
