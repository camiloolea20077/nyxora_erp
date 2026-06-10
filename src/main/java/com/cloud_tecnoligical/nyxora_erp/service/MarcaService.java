package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateMarcaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MarcaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MarcaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateMarcaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface MarcaService {
    Mono<MarcaResponseDto> create(CreateMarcaRequestDto dto);
    Mono<Boolean> update(UpdateMarcaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<MarcaResponseDto> findById(Long id);
    Mono<PageResponseDto<MarcaTableDto>> list(PageableDto<?> request);
}
