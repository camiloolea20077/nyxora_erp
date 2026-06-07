package com.cloud_tecnoligical.nyxora_erp.repository.permiso;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.permiso.PermisoDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

/** Catálogo GLOBAL de permisos (solo lectura, sin empresa_id). */
@Repository
public class PermisoQueryRepository {

    private final DatabaseClient db;

    public PermisoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<List<PermisoDto>> listAll() {
        return db.sql("""
                SELECT id AS "id", codigo AS "code", descripcion AS "description"
                FROM permiso ORDER BY codigo
                """)
            .fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, PermisoDto.class))
            .collectList();
    }
}
