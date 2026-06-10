package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CentroCostoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CentroCostoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateCentroCostoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateCentroCostoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface CentroCostoService {
    Mono<CentroCostoResponseDto> create(CreateCentroCostoRequestDto dto);
    Mono<Boolean> update(UpdateCentroCostoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<CentroCostoResponseDto> findById(Long id);
    Mono<PageResponseDto<CentroCostoTableDto>> list(PageableDto<?> request);
}
