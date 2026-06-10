package com.cloud_tecnoligical.nyxora_erp.repository.contabilidad;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.SaldoContableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

/**
 * Saldos contables = PROYECCIÓN recalculable desde movimiento_contable.
 * El recálculo borra y reconstruye los saldos del periodo (por cuenta) en una transacción.
 */
@Repository
public class SaldoContableQueryRepository {

    private final DatabaseClient db;

    public SaldoContableQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Long> borrarSaldosPeriodo(Long periodoId, Long empresaId) {
        return db.sql("DELETE FROM saldo_contable WHERE periodo_contable_id=:p AND empresa_id=:e")
            .bind("p", periodoId).bind("e", empresaId).fetch().rowsUpdated();
    }

    /**
     * Reconstruye saldos por cuenta desde los movimientos de comprobantes no-borrador del periodo.
     * v1: sin saldos de apertura (anterior=0); ejes tercero/centro_costo agregados (NULL).
     */
    public Mono<Long> reconstruirSaldosPeriodo(Long periodoId, Long empresaId) {
        return db.sql("""
                INSERT INTO saldo_contable (empresa_id, periodo_contable_id, cuenta_id,
                    saldo_debito_anterior, saldo_credito_anterior, debito_periodo, credito_periodo,
                    saldo_debito_final, saldo_credito_final, fecha_recalculo)
                SELECT m.empresa_id, c.periodo_contable_id, m.cuenta_id,
                       0, 0, SUM(m.debito), SUM(m.credito), SUM(m.debito), SUM(m.credito), now()
                FROM movimiento_contable m
                JOIN comprobante c ON c.id = m.comprobante_id
                WHERE c.periodo_contable_id = :p AND c.empresa_id = :e
                  AND c.deleted_at IS NULL AND c.estado <> 'borrador'
                GROUP BY m.empresa_id, c.periodo_contable_id, m.cuenta_id
                """)
            .bind("p", periodoId).bind("e", empresaId).fetch().rowsUpdated();
    }

    public Mono<List<SaldoContableResponseDto>> listByPeriodo(Long periodoId, Long cuentaId, Long empresaId) {
        StringBuilder sql = new StringBuilder("""
                SELECT s.id AS "id", s.periodo_contable_id AS "periodoContableId", s.cuenta_id AS "cuentaId",
                       s.tercero_id AS "terceroId", s.centro_costo_id AS "centroCostoId",
                       s.saldo_debito_anterior AS "saldoDebitoAnterior", s.saldo_credito_anterior AS "saldoCreditoAnterior",
                       s.debito_periodo AS "debitoPeriodo", s.credito_periodo AS "creditoPeriodo",
                       s.saldo_debito_final AS "saldoDebitoFinal", s.saldo_credito_final AS "saldoCreditoFinal"
                FROM saldo_contable s WHERE s.periodo_contable_id=:p AND s.empresa_id=:e
                """);
        if (cuentaId != null) {
            sql.append(" AND s.cuenta_id=:c ");
        }
        sql.append(" ORDER BY s.cuenta_id");

        var spec = db.sql(sql.toString()).bind("p", periodoId).bind("e", empresaId);
        if (cuentaId != null) {
            spec = spec.bind("c", cuentaId);
        }
        return spec.fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, SaldoContableResponseDto.class))
            .collectList();
    }
}
