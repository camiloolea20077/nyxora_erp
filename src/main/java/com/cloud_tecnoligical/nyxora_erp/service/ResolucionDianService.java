package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.CreateResolucionDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.ResolucionDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.ResolucionDianTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.UpdateResolucionDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ResolucionDianService {
    Mono<ResolucionDianResponseDto> create(CreateResolucionDianRequestDto dto);
    Mono<Boolean> update(UpdateResolucionDianRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ResolucionDianResponseDto> findById(Long id);
    Mono<PageResponseDto<ResolucionDianTableDto>> list(PageableDto<?> request);
}
