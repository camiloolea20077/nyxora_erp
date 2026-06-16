package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.CreateObligacionPagoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ObligacionPagoService {
    Mono<ObligacionPagoResponseDto> create(CreateObligacionPagoRequestDto dto);
    Mono<ObligacionPagoResponseDto> findById(Long id);
    Mono<PageResponseDto<ObligacionPagoTableDto>> list(PageableDto<?> request);
    Mono<Boolean> anular(Long id);
}
