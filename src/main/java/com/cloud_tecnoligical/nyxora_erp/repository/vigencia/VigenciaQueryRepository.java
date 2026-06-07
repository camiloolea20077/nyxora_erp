package com.cloud_tecnoligical.nyxora_erp.repository.vigencia;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.VigenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.VigenciaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class VigenciaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("anio", "estado", "created_at");

    public VigenciaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByAnio(Integer anio, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM vigencia WHERE anio=:a AND empresa_id=:e AND deleted_at IS NULL")
            .bind("a", anio).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByAnioExcludingId(Integer anio, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM vigencia WHERE anio=:a AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("a", anio).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<VigenciaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT v.id AS "id", v.anio AS "year", v.estado AS "status",
                       v.fecha_apertura AS "openDate", v.fecha_cierre AS "closeDate",
                       v.activo AS "active", v.created_at AS "createdAt"
                FROM vigencia v WHERE v.id=:id AND v.empresa_id=:e AND v.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, VigenciaResponseDto.class));
    }

    public Mono<PageResponseDto<VigenciaTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "anio";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        String sql = """
                SELECT v.id AS "id", v.anio AS "year", v.estado AS "status", v.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM vigencia v WHERE v.empresa_id=:e AND v.deleted_at IS NULL
                ORDER BY v.%s %s OFFSET :offset LIMIT :limit
                """.formatted(orderBy, order);

        return db.sql(sql).bind("e", empresaId).bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<VigenciaTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, VigenciaTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
