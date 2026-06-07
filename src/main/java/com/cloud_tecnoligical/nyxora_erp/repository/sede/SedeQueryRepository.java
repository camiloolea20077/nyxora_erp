package com.cloud_tecnoligical.nyxora_erp.repository.sede;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.SedeResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.SedeTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

/** Todo el filtrado (tenant + soft-delete + búsqueda + paginación) en SQL nativo reactivo. */
@Repository
public class SedeQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "created_at");

    public SedeQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsActiveByCodigo(String codigo, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM sede
                WHERE codigo = :codigo AND empresa_id = :empresaId AND deleted_at IS NULL
                """)
            .bind("codigo", codigo).bind("empresaId", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one()
            .map(c -> c > 0);
    }

    public Mono<Boolean> existsActiveByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM sede
                WHERE codigo = :codigo AND empresa_id = :empresaId AND id <> :id AND deleted_at IS NULL
                """)
            .bind("codigo", codigo).bind("empresaId", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one()
            .map(c -> c > 0);
    }

    public Mono<SedeResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT s.id         AS "id",
                       s.codigo     AS "code",
                       s.nombre     AS "name",
                       s.activo     AS "active",
                       s.created_at AS "createdAt"
                FROM sede s
                WHERE s.id = :id AND s.empresa_id = :empresaId AND s.deleted_at IS NULL
                LIMIT 1
                """)
            .bind("id", id).bind("empresaId", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, SedeResponseDto.class));
    }

    public Mono<PageResponseDto<SedeTableDto>> listSedes(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "nombre";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT s.id        AS "id",
                       s.codigo    AS "code",
                       s.nombre    AS "name",
                       s.activo    AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM sede s
                WHERE s.empresa_id = :empresaId AND s.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(s.nombre) LIKE LOWER(:search) OR LOWER(s.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY s.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("empresaId", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<SedeTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, SedeTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
