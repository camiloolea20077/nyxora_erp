package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreatePeriodoContableRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.PeriodoContableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.PeriodoContableTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface PeriodoContableService {
    Mono<PeriodoContableResponseDto> create(CreatePeriodoContableRequestDto dto);
    Mono<PeriodoContableResponseDto> findById(Long id);
    Mono<PageResponseDto<PeriodoContableTableDto>> list(PageableDto<?> request);
    Mono<Boolean> cerrar(Long id);
    Mono<Boolean> reabrir(Long id);
}
