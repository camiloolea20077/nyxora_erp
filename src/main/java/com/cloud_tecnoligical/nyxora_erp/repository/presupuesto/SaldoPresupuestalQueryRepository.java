package com.cloud_tecnoligical.nyxora_erp.repository.presupuesto;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.SaldoPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

@Repository
public class SaldoPresupuestalQueryRepository {

    private static final String SELECT = """
            SELECT s.id AS "id", s.rubro_presupuestal_id AS "rubroPresupuestalId", s.anio AS "anio", s.mes AS "mes",
                   s.plan_inicial AS "planInicial", s.adiciones AS "adiciones", s.reducciones AS "reducciones",
                   s.aplazamientos AS "aplazamientos", s.creditos AS "creditos", s.contra_creditos AS "contraCreditos",
                   s.disponibilidad AS "disponibilidad", s.compromiso AS "compromiso", s.obligacion AS "obligacion",
                   s.pagado AS "pagado", s.reconocimientos AS "reconocimientos", s.recaudos AS "recaudos",
                   (s.plan_inicial + s.adiciones - s.reducciones + s.creditos - s.contra_creditos - s.aplazamientos) AS "apropiacionNeta"
            FROM saldo_presupuestal s
            """;

    private final DatabaseClient db;

    public SaldoPresupuestalQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Long> findIdByRubroAnioMes(Long rubroId, Integer anio, Integer mes, Long empresaId) {
        return db.sql("SELECT id FROM saldo_presupuestal WHERE rubro_presupuestal_id=:r AND anio=:a AND mes=:m AND empresa_id=:e LIMIT 1")
            .bind("r", rubroId).bind("a", anio).bind("m", mes).bind("e", empresaId)
            .map(row -> ((Number) row.get("id")).longValue()).one();
    }

    public Mono<SaldoPresupuestalResponseDto> findByRubroAnioMes(Long rubroId, Integer anio, Integer mes, Long empresaId) {
        return db.sql(SELECT + " WHERE s.rubro_presupuestal_id=:r AND s.anio=:a AND s.mes=:m AND s.empresa_id=:e LIMIT 1")
            .bind("r", rubroId).bind("a", anio).bind("m", mes).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, SaldoPresupuestalResponseDto.class));
    }

    public Mono<List<SaldoPresupuestalResponseDto>> listByRubro(Long rubroId, Long empresaId) {
        return db.sql(SELECT + " WHERE s.rubro_presupuestal_id=:r AND s.empresa_id=:e ORDER BY s.anio, s.mes")
            .bind("r", rubroId).bind("e", empresaId)
            .fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, SaldoPresupuestalResponseDto.class))
            .collectList();
    }
}
