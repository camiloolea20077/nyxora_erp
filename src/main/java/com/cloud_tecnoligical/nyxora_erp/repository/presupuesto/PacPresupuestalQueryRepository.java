package com.cloud_tecnoligical.nyxora_erp.repository.presupuesto;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.PacPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

@Repository
public class PacPresupuestalQueryRepository {

    private final DatabaseClient db;

    public PacPresupuestalQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Long> findIdByRubroAnioMes(Long rubroId, Integer anio, Integer mes, Long empresaId) {
        return db.sql("SELECT id FROM pac_presupuestal WHERE rubro_presupuestal_id=:r AND anio=:a AND mes=:m AND empresa_id=:e LIMIT 1")
            .bind("r", rubroId).bind("a", anio).bind("m", mes).bind("e", empresaId)
            .map(row -> ((Number) row.get("id")).longValue()).one();
    }

    public Mono<List<PacPresupuestalResponseDto>> listByRubroAnio(Long rubroId, Integer anio, Long empresaId) {
        return db.sql("""
                SELECT id AS "id", rubro_presupuestal_id AS "rubroPresupuestalId", anio AS "anio",
                       mes AS "mes", valor AS "valor"
                FROM pac_presupuestal WHERE rubro_presupuestal_id=:r AND anio=:a AND empresa_id=:e ORDER BY mes
                """)
            .bind("r", rubroId).bind("a", anio).bind("e", empresaId)
            .fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, PacPresupuestalResponseDto.class))
            .collectList();
    }
}
