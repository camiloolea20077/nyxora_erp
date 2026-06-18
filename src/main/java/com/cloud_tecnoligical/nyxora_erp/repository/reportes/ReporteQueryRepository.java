package com.cloud_tecnoligical.nyxora_erp.repository.reportes;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.reportes.BalanceLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.CarteraTerceroDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.EjecucionRubroDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

/**
 * Reportes (solo lectura) sobre las proyecciones existentes. La clase contable se deriva del
 * primer dígito del código PUC (1=activo, 2=pasivo, 3=patrimonio, 4=ingreso, 5/6/7=costo/gasto).
 */
@Repository
public class ReporteQueryRepository {

    private final DatabaseClient db;

    public ReporteQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    /** Líneas de balance (clases 1,2,3) o de resultados (4,5,6,7), según el patrón de clases. */
    private Mono<List<BalanceLineaDto>> lineas(Long periodoContableId, Long empresaId, String clasesIn) {
        return db.sql("""
                SELECT LEFT(c.codigo_cuenta, 1) AS "clase", c.codigo_cuenta AS "codigoCuenta",
                       c.nombre_cuenta AS "nombreCuenta",
                       COALESCE(SUM(s.saldo_debito_final), 0) AS "debito",
                       COALESCE(SUM(s.saldo_credito_final), 0) AS "credito",
                       COALESCE(SUM(s.saldo_debito_final), 0) - COALESCE(SUM(s.saldo_credito_final), 0) AS "saldo"
                FROM saldo_contable s
                JOIN cuenta c ON c.id = s.cuenta_id
                WHERE s.periodo_contable_id = :p AND s.empresa_id = :e AND c.maneja_movimiento = TRUE
                  AND LEFT(c.codigo_cuenta, 1) IN (""" + clasesIn + """
                  )
                GROUP BY c.id, c.codigo_cuenta, c.nombre_cuenta
                HAVING COALESCE(SUM(s.saldo_debito_final), 0) <> 0 OR COALESCE(SUM(s.saldo_credito_final), 0) <> 0
                ORDER BY c.codigo_cuenta
                """)
            .bind("p", periodoContableId).bind("e", empresaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, BalanceLineaDto.class))
            .collectList();
    }

    public Mono<List<BalanceLineaDto>> lineasBalance(Long periodoContableId, Long empresaId) {
        return lineas(periodoContableId, empresaId, "'1','2','3'");
    }

    public Mono<List<BalanceLineaDto>> lineasResultados(Long periodoContableId, Long empresaId) {
        return lineas(periodoContableId, empresaId, "'4','5','6','7'");
    }

    public Mono<List<CarteraTerceroDto>> cartera(Long empresaId) {
        return db.sql("""
                SELECT cxc.cliente_id AS "clienteId", t.nombre AS "clienteNombre", COUNT(*) AS "documentos",
                       COALESCE(SUM(cxc.saldo), 0) AS "saldoTotal",
                       COALESCE(SUM(CASE WHEN cxc.fecha_vencimiento < CURRENT_DATE THEN cxc.saldo ELSE 0 END), 0) AS "saldoVencido"
                FROM cuenta_por_cobrar cxc
                JOIN tercero t ON t.id = cxc.cliente_id
                WHERE cxc.empresa_id = :e AND cxc.deleted_at IS NULL
                  AND cxc.estado IN ('vigente', 'en_acuerdo') AND cxc.saldo > 0
                GROUP BY cxc.cliente_id, t.nombre
                ORDER BY "saldoTotal" DESC
                """)
            .bind("e", empresaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, CarteraTerceroDto.class))
            .collectList();
    }

    public Mono<List<EjecucionRubroDto>> ejecucionPresupuestal(Long empresaId, Long vigenciaId) {
        return db.sql("""
                SELECT r.id AS "rubroId", r.codigo_rubro AS "codigoRubro", r.nombre_rubro AS "nombreRubro",
                       r.tipo_rubro AS "tipoRubro",
                       COALESCE(SUM(s.plan_inicial + s.adiciones - s.reducciones + s.creditos - s.contra_creditos - s.aplazamientos), 0) AS "apropiacion",
                       COALESCE(SUM(s.compromiso), 0) AS "comprometido",
                       COALESCE(SUM(s.obligacion), 0) AS "obligado",
                       COALESCE(SUM(s.pagado), 0) AS "pagado"
                FROM saldo_presupuestal s
                JOIN rubro_presupuestal r ON r.id = s.rubro_presupuestal_id
                WHERE s.empresa_id = :e AND r.vigencia_id = :v AND r.deleted_at IS NULL
                GROUP BY r.id, r.codigo_rubro, r.nombre_rubro, r.tipo_rubro
                ORDER BY r.codigo_rubro
                """)
            .bind("e", empresaId).bind("v", vigenciaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, EjecucionRubroDto.class))
            .collectList();
    }

    public Mono<Long> contarComprobantesBorrador(Long periodoContableId, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM comprobante
                WHERE periodo_contable_id = :p AND empresa_id = :e AND deleted_at IS NULL AND estado = 'borrador'
                """)
            .bind("p", periodoContableId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one();
    }
}
