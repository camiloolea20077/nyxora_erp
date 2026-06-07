package com.cloud_tecnoligical.nyxora_erp.repository.auth;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.auth.UsuarioAuthDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Filtrado de autenticación con SQL nativo reactivo (DatabaseClient).
 * Alias entre comillas camelCase para que MapperRepository (match exacto) mapee al DTO.
 */
@Repository
public class AuthQueryRepository {

    private final DatabaseClient db;

    public AuthQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<UsuarioAuthDto> findActiveByUsername(String username) {
        String sql = """
            SELECT u.id            AS "id",
                   u.empresa_id    AS "empresaId",
                   u.hash_password AS "hashPassword",
                   u.activo        AS "activo"
            FROM usuario u
            WHERE u.username = :username
              AND u.activo = true
              AND u.deleted_at IS NULL
            LIMIT 1
            """;
        return db.sql(sql)
            .bind("username", username)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, UsuarioAuthDto.class));
    }

    /** Códigos de permiso efectivos del usuario (vía rol_permiso + usuario_rol). */
    public Flux<String> findPermisosByUsuario(Long usuarioId) {
        String sql = """
            SELECT DISTINCT p.codigo AS "codigo"
            FROM permiso p
            JOIN rol_permiso rp ON rp.permiso_id = p.id
            JOIN usuario_rol ur ON ur.rol_id = rp.rol_id
            WHERE ur.usuario_id = :usuarioId
            """;
        return db.sql(sql)
            .bind("usuarioId", usuarioId)
            .fetch().all()
            .map(row -> (String) row.get("codigo"));
    }
}
