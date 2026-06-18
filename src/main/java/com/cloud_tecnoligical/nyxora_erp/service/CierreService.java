package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.reportes.CierrePeriodoResultDto;

import reactor.core.publisher.Mono;

public interface CierreService {
    /** Cierre orquestado de un periodo contable: valida → recalcula saldos → cierra. */
    Mono<CierrePeriodoResultDto> cerrarPeriodo(Long periodoContableId);
}
