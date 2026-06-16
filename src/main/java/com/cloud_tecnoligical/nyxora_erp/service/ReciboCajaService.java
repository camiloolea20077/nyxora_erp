package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateReciboCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ReciboCajaService {
    Mono<ReciboCajaResponseDto> create(CreateReciboCajaRequestDto dto);
    Mono<ReciboCajaResponseDto> findById(Long id);
    Mono<PageResponseDto<ReciboCajaTableDto>> list(PageableDto<?> request);
    Mono<Boolean> anular(Long id);
}
