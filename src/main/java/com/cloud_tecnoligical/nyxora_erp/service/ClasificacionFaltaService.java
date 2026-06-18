package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ClasificacionFaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ClasificacionFaltaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateClasificacionFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.UpdateClasificacionFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ClasificacionFaltaService {
    Mono<ClasificacionFaltaResponseDto> create(CreateClasificacionFaltaRequestDto dto);
    Mono<Boolean> update(UpdateClasificacionFaltaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ClasificacionFaltaResponseDto> findById(Long id);
    Mono<PageResponseDto<ClasificacionFaltaTableDto>> list(PageableDto<?> request);
}
