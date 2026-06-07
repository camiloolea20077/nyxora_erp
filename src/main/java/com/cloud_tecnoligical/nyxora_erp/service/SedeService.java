package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.CreateSedeRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.SedeResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.SedeTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.UpdateSedeRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface SedeService {
    Mono<SedeResponseDto> create(CreateSedeRequestDto dto);
    Mono<Boolean> update(UpdateSedeRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<SedeResponseDto> findById(Long id);
    Mono<PageResponseDto<SedeTableDto>> list(PageableDto<?> request);
}
