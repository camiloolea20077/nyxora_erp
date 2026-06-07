package com.cloud_tecnoligical.nyxora_erp.repository.rol;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.RolResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.RolTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RolQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("nombre", "created_at");

    public RolQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsActiveByNombre(String nombre, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM rol WHERE nombre=:n AND empresa_id=:e AND deleted_at IS NULL")
            .bind("n", nombre).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsActiveByNombreExcludingId(String nombre, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM rol WHERE nombre=:n AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("n", nombre).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<RolResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT r.id AS "id", r.nombre AS "name", r.activo AS "active", r.created_at AS "createdAt"
                FROM rol r WHERE r.id=:id AND r.empresa_id=:e AND r.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, RolResponseDto.class));
    }

    public Mono<List<Long>> findPermisoIds(Long rolId) {
        return db.sql("SELECT permiso_id FROM rol_permiso WHERE rol_id=:r")
            .bind("r", rolId)
            .map(row -> ((Number) row.get("permiso_id")).longValue())
            .all().collectList();
    }

    public Mono<PageResponseDto<RolTableDto>> listRoles(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "nombre";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT r.id AS "id", r.nombre AS "name", r.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM rol r WHERE r.empresa_id=:e AND r.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND LOWER(r.nombre) LIKE LOWER(:search) ");
        }
        sql.append(" ORDER BY r.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<RolTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, RolTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    /** Reemplaza el set de permisos del rol (borra y reinserta). */
    public Mono<Void> setPermisos(Long rolId, List<Long> permisoIds) {
        Mono<Long> del = db.sql("DELETE FROM rol_permiso WHERE rol_id=:r").bind("r", rolId).fetch().rowsUpdated();
        if (permisoIds == null || permisoIds.isEmpty()) {
            return del.then();
        }
        return del.thenMany(Flux.fromIterable(permisoIds)
                .flatMap(pid -> db.sql("""
                        INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (:r,:p)
                        ON CONFLICT (rol_id, permiso_id) DO NOTHING
                        """)
                    .bind("r", rolId).bind("p", pid).fetch().rowsUpdated()))
            .then();
    }
}
