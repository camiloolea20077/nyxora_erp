package com.cloud_tecnoligical.nyxora_erp.repository.documento;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

/**
 * Numeración consecutiva atómica por (tipo_documento, sede, vigencia).
 *
 * El incremento se hace bajo bloqueo de fila (SELECT ... FOR UPDATE) DENTRO de una
 * transacción (el Service lo envuelve con TransactionalOperator). La fila de consecutivo
 * se crea perezosamente en 0 con ON CONFLICT DO NOTHING para soportar la primera vez sin
 * condiciones de carrera en la creación.
 */
@Repository
public class ConsecutivoQueryRepository {

    private final DatabaseClient db;

    public ConsecutivoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    /**
     * Asegura la fila, la bloquea, incrementa y devuelve el nuevo número.
     * DEBE ejecutarse dentro de una transacción para que el FOR UPDATE tenga efecto.
     */
    public Mono<Long> incrementarYObtener(Long tipoDocumentoId, Long sedeId, Long vigenciaId) {
        Mono<Long> asegurarFila = db.sql("""
                INSERT INTO consecutivo (tipo_documento_id, sede_id, vigencia_id, ultimo_numero)
                VALUES (:t, :s, :v, 0)
                ON CONFLICT (tipo_documento_id, sede_id, vigencia_id) DO NOTHING
                """)
            .bind("t", tipoDocumentoId).bind("s", sedeId).bind("v", vigenciaId)
            .fetch().rowsUpdated();

        Mono<Long> bloquearYLeer = db.sql("""
                SELECT ultimo_numero FROM consecutivo
                WHERE tipo_documento_id=:t AND sede_id=:s AND vigencia_id=:v
                FOR UPDATE
                """)
            .bind("t", tipoDocumentoId).bind("s", sedeId).bind("v", vigenciaId)
            .map(row -> ((Number) row.get("ultimo_numero")).longValue())
            .one();

        return asegurarFila
            .then(bloquearYLeer)
            .flatMap(actual -> {
                long nuevo = actual + 1;
                return db.sql("""
                        UPDATE consecutivo SET ultimo_numero=:n
                        WHERE tipo_documento_id=:t AND sede_id=:s AND vigencia_id=:v
                        """)
                    .bind("n", nuevo).bind("t", tipoDocumentoId).bind("s", sedeId).bind("v", vigenciaId)
                    .fetch().rowsUpdated()
                    .thenReturn(nuevo);
            });
    }
}
