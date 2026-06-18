package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.FaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.FaltaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.UpdateFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface FaltaService {
    Mono<FaltaResponseDto> create(CreateFaltaRequestDto dto);
    Mono<Boolean> update(UpdateFaltaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<FaltaResponseDto> findById(Long id);
    Mono<PageResponseDto<FaltaTableDto>> list(PageableDto<?> request);
}
