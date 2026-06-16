package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreatePolizaSeguroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.PolizaSeguroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.PolizaSeguroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.UpdatePolizaSeguroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface PolizaSeguroService {
    Mono<PolizaSeguroResponseDto> create(CreatePolizaSeguroRequestDto dto);
    Mono<Boolean> update(UpdatePolizaSeguroRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<PolizaSeguroResponseDto> findById(Long id);
    Mono<PageResponseDto<PolizaSeguroTableDto>> list(PageableDto<?> request);
}
