package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.categoria.CategoriaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.categoria.CategoriaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.categoria.CreateCategoriaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.categoria.UpdateCategoriaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface CategoriaService {
    Mono<CategoriaResponseDto> create(CreateCategoriaRequestDto dto);
    Mono<Boolean> update(UpdateCategoriaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<CategoriaResponseDto> findById(Long id);
    Mono<PageResponseDto<CategoriaTableDto>> list(PageableDto<?> request);
}
