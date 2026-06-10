package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateProyectoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.ProyectoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.ProyectoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateProyectoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ProyectoService {
    Mono<ProyectoResponseDto> create(CreateProyectoRequestDto dto);
    Mono<Boolean> update(UpdateProyectoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ProyectoResponseDto> findById(Long id);
    Mono<PageResponseDto<ProyectoTableDto>> list(PageableDto<?> request);
}
