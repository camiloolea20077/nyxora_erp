package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEvaluacionProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionProgramaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionProgramaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEvaluacionProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface EvaluacionProgramaService {
    Mono<EvaluacionProgramaResponseDto> create(CreateEvaluacionProgramaDto dto);
    Mono<Boolean> update(UpdateEvaluacionProgramaDto dto);
    Mono<Boolean> delete(Long id);
    Mono<EvaluacionProgramaResponseDto> findById(Long id);
    Mono<PageResponseDto<EvaluacionProgramaTableDto>> list(PageableDto<?> request);
}
