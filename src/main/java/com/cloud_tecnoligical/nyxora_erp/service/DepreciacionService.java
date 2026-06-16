package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreateDepreciacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.DepreciacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.DepreciacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface DepreciacionService {
    Mono<DepreciacionResponseDto> registrar(CreateDepreciacionRequestDto dto);
    Mono<DepreciacionResponseDto> findById(Long id);
    Mono<PageResponseDto<DepreciacionTableDto>> listByActivo(Long activoFijoId, PageableDto<?> request);
}
