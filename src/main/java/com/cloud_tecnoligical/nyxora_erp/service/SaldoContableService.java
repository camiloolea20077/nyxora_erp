package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.SaldoContableResponseDto;

import reactor.core.publisher.Mono;

public interface SaldoContableService {

    /** Recalcula (borra + reconstruye) los saldos del periodo. Devuelve filas generadas. */
    Mono<Long> recalcular(Long periodoContableId);

    Mono<List<SaldoContableResponseDto>> consultar(Long periodoContableId, Long cuentaId);
}
