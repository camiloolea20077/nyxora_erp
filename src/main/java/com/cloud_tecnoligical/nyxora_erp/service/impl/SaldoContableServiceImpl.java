package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.SaldoContableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.PeriodoContableQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.SaldoContableQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.SaldoContableService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class SaldoContableServiceImpl implements SaldoContableService {

    private final SaldoContableQueryRepository saldoQuery;
    private final PeriodoContableQueryRepository periodoQuery;
    private final TransactionalOperator tx;

    public SaldoContableServiceImpl(SaldoContableQueryRepository saldoQuery,
                                    PeriodoContableQueryRepository periodoQuery,
                                    ReactiveTransactionManager txManager) {
        this.saldoQuery = saldoQuery;
        this.periodoQuery = periodoQuery;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<Long> recalcular(Long periodoContableId) {
        return TenantContext.get().flatMap(t ->
            periodoQuery.findEstado(periodoContableId, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Periodo contable no encontrado")))
                .then(saldoQuery.borrarSaldosPeriodo(periodoContableId, t.getEmpresaId())
                    .then(saldoQuery.reconstruirSaldosPeriodo(periodoContableId, t.getEmpresaId()))
                    .as(tx::transactional)));
    }

    @Override
    public Mono<List<SaldoContableResponseDto>> consultar(Long periodoContableId, Long cuentaId) {
        return TenantContext.get().flatMap(t ->
            saldoQuery.listByPeriodo(periodoContableId, cuentaId, t.getEmpresaId()));
    }
}
