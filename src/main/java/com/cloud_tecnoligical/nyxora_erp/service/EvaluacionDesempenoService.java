package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEvaluacionDesempenoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionDesempenoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionDesempenoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEvaluacionDesempenoDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface EvaluacionDesempenoService {
    Mono<EvaluacionDesempenoResponseDto> create(CreateEvaluacionDesempenoDto dto);
    Mono<Boolean> update(UpdateEvaluacionDesempenoDto dto);
    Mono<Boolean> delete(Long id);
    Mono<EvaluacionDesempenoResponseDto> findById(Long id);
    Mono<PageResponseDto<EvaluacionDesempenoTableDto>> list(PageableDto<?> request, Long empleadoId, Long programaId);
}
