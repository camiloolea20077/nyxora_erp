package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.PacPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.PacUpsertRequestDto;

import reactor.core.publisher.Mono;

public interface PacPresupuestalService {
    Mono<PacPresupuestalResponseDto> upsert(PacUpsertRequestDto dto);
    Mono<List<PacPresupuestalResponseDto>> listByRubroAnio(Long rubroId, Integer anio);
}
