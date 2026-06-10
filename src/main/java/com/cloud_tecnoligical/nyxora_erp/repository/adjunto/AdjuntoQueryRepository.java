package com.cloud_tecnoligical.nyxora_erp.repository.adjunto;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.adjunto.AdjuntoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

@Repository
public class AdjuntoQueryRepository {

    private final DatabaseClient db;

    public AdjuntoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<AdjuntoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT a.id AS "id", a.modulo AS "modulo", a.entidad AS "entidad", a.entidad_id AS "entidadId",
                       a.nombre AS "nombre", a.tipo_mime AS "tipoMime", a.url AS "url",
                       a.tamano_bytes AS "tamanoBytes", a.descripcion AS "descripcion",
                       a.activo AS "active", a.created_at AS "createdAt"
                FROM adjunto a WHERE a.id=:id AND a.empresa_id=:e AND a.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, AdjuntoResponseDto.class));
    }

    /** Lista los adjuntos vigentes de un objeto (modulo, entidad, entidad_id) de la empresa. */
    public Mono<List<AdjuntoResponseDto>> listByObjeto(String modulo, String entidad, Long entidadId, Long empresaId) {
        return db.sql("""
                SELECT a.id AS "id", a.modulo AS "modulo", a.entidad AS "entidad", a.entidad_id AS "entidadId",
                       a.nombre AS "nombre", a.tipo_mime AS "tipoMime", a.url AS "url",
                       a.tamano_bytes AS "tamanoBytes", a.descripcion AS "descripcion",
                       a.activo AS "active", a.created_at AS "createdAt"
                FROM adjunto a
                WHERE a.empresa_id=:e AND a.modulo=:m AND a.entidad=:en AND a.entidad_id=:eid AND a.deleted_at IS NULL
                ORDER BY a.id
                """)
            .bind("e", empresaId).bind("m", modulo).bind("en", entidad).bind("eid", entidadId)
            .fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, AdjuntoResponseDto.class))
            .collectList();
    }
}
