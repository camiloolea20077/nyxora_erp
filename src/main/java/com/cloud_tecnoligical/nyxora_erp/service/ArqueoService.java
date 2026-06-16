package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.ArqueoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ArqueoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateArqueoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ArqueoService {
    Mono<ArqueoResponseDto> create(CreateArqueoRequestDto dto);
    Mono<ArqueoResponseDto> findById(Long id);
    Mono<PageResponseDto<ArqueoTableDto>> list(PageableDto<?> request);
}
