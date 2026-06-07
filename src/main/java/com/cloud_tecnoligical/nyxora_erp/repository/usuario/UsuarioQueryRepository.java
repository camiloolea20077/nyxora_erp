package com.cloud_tecnoligical.nyxora_erp.repository.usuario;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UsuarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UsuarioTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class UsuarioQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("username", "email", "created_at");

    public UsuarioQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsActiveByUsername(String username, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM usuario
                WHERE username = :username AND empresa_id = :empresaId AND deleted_at IS NULL
                """)
            .bind("username", username).bind("empresaId", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** El tercero existe, pertenece a la empresa y no está eliminado. */
    public Mono<Boolean> existsTerceroValido(Long terceroId, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM tercero
                WHERE id = :terceroId AND empresa_id = :empresaId AND deleted_at IS NULL
                """)
            .bind("terceroId", terceroId).bind("empresaId", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** ¿El tercero ya tiene un usuario activo? (un usuario por tercero) */
    public Mono<Boolean> existsUsuarioActivoByTercero(Long terceroId, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM usuario
                WHERE tercero_id = :terceroId AND empresa_id = :empresaId AND deleted_at IS NULL
                """)
            .bind("terceroId", terceroId).bind("empresaId", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<UsuarioResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT u.id         AS "id",
                       u.tercero_id AS "terceroId",
                       t.nombre     AS "terceroNombre",
                       u.username   AS "username",
                       u.email      AS "email",
                       u.activo     AS "active",
                       u.created_at AS "createdAt"
                FROM usuario u
                LEFT JOIN tercero t ON t.id = u.tercero_id
                WHERE u.id = :id AND u.empresa_id = :empresaId AND u.deleted_at IS NULL
                LIMIT 1
                """)
            .bind("id", id).bind("empresaId", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, UsuarioResponseDto.class));
    }

    public Mono<PageResponseDto<UsuarioTableDto>> listUsuarios(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "username";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT u.id       AS "id",
                       u.username AS "username",
                       u.email    AS "email",
                       t.nombre   AS "terceroNombre",
                       u.activo   AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM usuario u
                LEFT JOIN tercero t ON t.id = u.tercero_id
                WHERE u.empresa_id = :empresaId AND u.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(u.username) LIKE LOWER(:search) OR LOWER(u.email) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY u.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("empresaId", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<UsuarioTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, UsuarioTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    /** Asigna un rol al usuario en una sede (idempotente). */
    public Mono<Long> asignarRol(Long usuarioId, Long rolId, Long sedeId) {
        return db.sql("""
                INSERT INTO usuario_rol (usuario_id, rol_id, sede_id)
                VALUES (:u, :r, :s)
                ON CONFLICT (usuario_id, rol_id, sede_id) DO NOTHING
                """)
            .bind("u", usuarioId).bind("r", rolId).bind("s", sedeId)
            .fetch().rowsUpdated();
    }

    public Mono<Long> quitarRol(Long usuarioId, Long rolId, Long sedeId) {
        return db.sql("DELETE FROM usuario_rol WHERE usuario_id=:u AND rol_id=:r AND sede_id=:s")
            .bind("u", usuarioId).bind("r", rolId).bind("s", sedeId)
            .fetch().rowsUpdated();
    }
}
