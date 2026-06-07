package com.cloud_tecnoligical.nyxora_erp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Seeder de datos DEMO (dev): crea una empresa + sede + rol admin (con todos los permisos)
 * + usuario admin, para poder probar el login. Idempotente: solo si no existe ninguna empresa.
 * Controlado por app.seed-demo. NO usar en producción.
 */
@Component
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private final DatabaseClient db;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedDemo;

    public DevDataSeeder(DatabaseClient db, PasswordEncoder passwordEncoder,
                         @Value("${app.seed-demo:false}") boolean seedDemo) {
        this.db = db;
        this.passwordEncoder = passwordEncoder;
        this.seedDemo = seedDemo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!seedDemo) {
            return;
        }
        contar("empresa")
            .flatMap(total -> total > 0 ? Mono.empty() : sembrar())
            .doOnError(e -> log.warn("DevDataSeeder no pudo sembrar: {}", e.getMessage()))
            .onErrorResume(e -> Mono.empty())
            .block();
    }

    private Mono<Void> sembrar() {
        String hash = passwordEncoder.encode("admin123");
        return insertReturningId("""
                INSERT INTO empresa (nit, razon_social, tipo_persona) VALUES ('900123456','Empresa Demo S.A.S.','juridica') RETURNING id
                """)
            .flatMap(empresaId -> insertReturningId("""
                    INSERT INTO sede (empresa_id, codigo, nombre) VALUES (:e,'PRIN','Sede Principal') RETURNING id
                    """, "e", empresaId)
                .flatMap(sedeId -> insertReturningId("""
                        INSERT INTO rol (empresa_id, nombre) VALUES (:e,'Administrador') RETURNING id
                        """, "e", empresaId)
                    .flatMap(rolId -> insertReturningId("""
                            INSERT INTO usuario (empresa_id, username, email, hash_password)
                            VALUES (:e,'admin','admin@nyxora.local',:h) RETURNING id
                            """, "e", empresaId, "h", hash)
                        .flatMap(usuarioId ->
                            db.sql("INSERT INTO rol_permiso (rol_id, permiso_id) SELECT :r, id FROM permiso")
                                .bind("r", rolId).fetch().rowsUpdated()
                            .then(db.sql("INSERT INTO usuario_rol (usuario_id, rol_id, sede_id) VALUES (:u,:r,:s)")
                                .bind("u", usuarioId).bind("r", rolId).bind("s", sedeId).fetch().rowsUpdated())
                            .doOnSuccess(x -> log.info("DEMO sembrado: empresa={} usuario=admin / admin123", empresaId))
                            .then()))))
            .then();
    }

    private Mono<Long> contar(String tabla) {
        return db.sql("SELECT count(*) AS c FROM " + tabla)
            .map(row -> ((Number) row.get("c")).longValue()).one();
    }

    private Mono<Long> insertReturningId(String sql) {
        return db.sql(sql).map(row -> ((Number) row.get("id")).longValue()).one();
    }

    private Mono<Long> insertReturningId(String sql, String k, Object v) {
        return db.sql(sql).bind(k, v).map(row -> ((Number) row.get("id")).longValue()).one();
    }

    private Mono<Long> insertReturningId(String sql, String k1, Object v1, String k2, Object v2) {
        return db.sql(sql).bind(k1, v1).bind(k2, v2)
            .map(row -> ((Number) row.get("id")).longValue()).one();
    }
}
