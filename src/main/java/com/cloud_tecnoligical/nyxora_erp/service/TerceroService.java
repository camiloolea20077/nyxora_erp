package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface TerceroService {
    Mono<TerceroResponseDto> create(CreateTerceroRequestDto dto);
    Mono<Boolean> update(UpdateTerceroRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<TerceroResponseDto> findById(Long id);
    Mono<PageResponseDto<TerceroTableDto>> list(PageableDto<?> request);
}
