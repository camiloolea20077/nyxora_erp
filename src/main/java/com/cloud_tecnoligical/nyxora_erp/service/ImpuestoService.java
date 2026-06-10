package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.CreateImpuestoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.ImpuestoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.ImpuestoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.UpdateImpuestoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ImpuestoService {
    Mono<ImpuestoResponseDto> create(CreateImpuestoRequestDto dto);
    Mono<Boolean> update(UpdateImpuestoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ImpuestoResponseDto> findById(Long id);
    Mono<PageResponseDto<ImpuestoTableDto>> list(PageableDto<?> request);
}
