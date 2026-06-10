package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.SaldoInventarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.SaldoInventarioQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.SaldoInventarioService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class SaldoInventarioServiceImpl implements SaldoInventarioService {

    private final SaldoInventarioQueryRepository saldoQuery;
    private final BodegaQueryRepository bodegaQuery;
    private final TransactionalOperator tx;

    public SaldoInventarioServiceImpl(SaldoInventarioQueryRepository saldoQuery,
                                      BodegaQueryRepository bodegaQuery,
                                      ReactiveTransactionManager txManager) {
        this.saldoQuery = saldoQuery;
        this.bodegaQuery = bodegaQuery;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<Long> recalcular(Long bodegaId) {
        return TenantContext.get().flatMap(t ->
            bodegaQuery.existsActivaEnEmpresa(bodegaId, t.getEmpresaId())
                .flatMap(ok -> {
                    if (!Boolean.TRUE.equals(ok)) {
                        return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Bodega no encontrada"));
                    }
                    return saldoQuery.borrarSaldosBodega(bodegaId, t.getEmpresaId())
                        .then(saldoQuery.reconstruirSaldosBodega(bodegaId, t.getEmpresaId()))
                        .as(tx::transactional);
                }));
    }

    @Override
    public Mono<List<SaldoInventarioResponseDto>> consultar(Long bodegaId, Long productoId) {
        return TenantContext.get().flatMap(t ->
            saldoQuery.listByBodega(bodegaId, productoId, t.getEmpresaId()));
    }
}
