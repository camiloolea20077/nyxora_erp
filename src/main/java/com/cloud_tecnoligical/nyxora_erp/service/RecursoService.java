package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.CreateRecursoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.RecursoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.RecursoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.UpdateRecursoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface RecursoService {
    Mono<RecursoResponseDto> create(CreateRecursoRequestDto dto);
    Mono<Boolean> update(UpdateRecursoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<RecursoResponseDto> findById(Long id);
    Mono<PageResponseDto<RecursoTableDto>> list(PageableDto<?> request);
}
