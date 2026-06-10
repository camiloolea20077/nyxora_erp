package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateBodegaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateBodegaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface BodegaService {
    Mono<BodegaResponseDto> create(CreateBodegaRequestDto dto);
    Mono<Boolean> update(UpdateBodegaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<BodegaResponseDto> findById(Long id);
    Mono<PageResponseDto<BodegaTableDto>> list(PageableDto<?> request);
}
