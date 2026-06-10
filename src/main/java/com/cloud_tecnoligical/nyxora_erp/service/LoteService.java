package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateLoteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.LoteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.LoteTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateLoteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface LoteService {
    Mono<LoteResponseDto> create(CreateLoteRequestDto dto);
    Mono<Boolean> update(UpdateLoteRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<LoteResponseDto> findById(Long id);
    Mono<PageResponseDto<LoteTableDto>> list(PageableDto<?> request);
}
