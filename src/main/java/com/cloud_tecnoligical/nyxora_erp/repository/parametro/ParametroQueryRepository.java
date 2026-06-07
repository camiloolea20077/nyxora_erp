package com.cloud_tecnoligical.nyxora_erp.repository.parametro;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.ParametroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.ParametroTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ParametroQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("clave", "created_at");

    public ParametroQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByClave(String clave, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM parametro WHERE clave=:k AND empresa_id=:e AND deleted_at IS NULL")
            .bind("k", clave).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    private static final String SELECT_DTO = """
            SELECT p.id AS "id", p.clave AS "key", p.valor AS "value", p.tipo_dato AS "dataType",
                   p.activo AS "active", p.created_at AS "createdAt"
            FROM parametro p
            """;

    public Mono<ParametroResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql(SELECT_DTO + " WHERE p.id=:id AND p.empresa_id=:e AND p.deleted_at IS NULL LIMIT 1")
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ParametroResponseDto.class));
    }

    public Mono<ParametroResponseDto> findActiveByClave(String clave, Long empresaId) {
        return db.sql(SELECT_DTO + " WHERE p.clave=:k AND p.empresa_id=:e AND p.deleted_at IS NULL LIMIT 1")
            .bind("k", clave).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ParametroResponseDto.class));
    }

    public Mono<PageResponseDto<ParametroTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "clave";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT p.id AS "id", p.clave AS "key", p.valor AS "value", p.tipo_dato AS "dataType",
                       p.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM parametro p WHERE p.empresa_id=:e AND p.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND LOWER(p.clave) LIKE LOWER(:search) ");
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
            List<ParametroTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ParametroTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
