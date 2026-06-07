package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.CreateVigenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.UpdateVigenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.VigenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.VigenciaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface VigenciaService {
    Mono<VigenciaResponseDto> create(CreateVigenciaRequestDto dto);
    Mono<Boolean> update(UpdateVigenciaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<VigenciaResponseDto> findById(Long id);
    Mono<PageResponseDto<VigenciaTableDto>> list(PageableDto<?> request);
    Mono<Boolean> abrir(Long id);
    Mono<Boolean> cerrar(Long id);
}
