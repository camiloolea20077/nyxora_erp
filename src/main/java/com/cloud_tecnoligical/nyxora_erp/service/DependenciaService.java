package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateDependenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.DependenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.DependenciaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateDependenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface DependenciaService {
    Mono<DependenciaResponseDto> create(CreateDependenciaRequestDto dto);
    Mono<Boolean> update(UpdateDependenciaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<DependenciaResponseDto> findById(Long id);
    Mono<PageResponseDto<DependenciaTableDto>> list(PageableDto<?> request);
}
