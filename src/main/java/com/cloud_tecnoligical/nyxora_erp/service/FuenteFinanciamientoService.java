package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateFuenteFinanciamientoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.FuenteFinanciamientoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.FuenteFinanciamientoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.UpdateFuenteFinanciamientoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface FuenteFinanciamientoService {
    Mono<FuenteFinanciamientoResponseDto> create(CreateFuenteFinanciamientoRequestDto dto);
    Mono<Boolean> update(UpdateFuenteFinanciamientoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<FuenteFinanciamientoResponseDto> findById(Long id);
    Mono<PageResponseDto<FuenteFinanciamientoTableDto>> list(PageableDto<?> request);
}
