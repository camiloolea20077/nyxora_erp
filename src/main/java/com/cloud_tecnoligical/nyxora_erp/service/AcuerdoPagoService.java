package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CreateAcuerdoPagoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface AcuerdoPagoService {
    Mono<AcuerdoPagoResponseDto> create(CreateAcuerdoPagoRequestDto dto);
    Mono<AcuerdoPagoResponseDto> findById(Long id);
    Mono<PageResponseDto<AcuerdoPagoTableDto>> list(PageableDto<?> request);
    Mono<Boolean> anular(Long id);
}
