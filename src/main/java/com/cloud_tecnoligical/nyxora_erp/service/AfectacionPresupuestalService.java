package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.AfectacionPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.AfectacionPresupuestalTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateAfectacionPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface AfectacionPresupuestalService {
    Mono<AfectacionPresupuestalResponseDto> registrar(CreateAfectacionPresupuestalRequestDto dto);
    Mono<AfectacionPresupuestalResponseDto> findById(Long id);
    Mono<PageResponseDto<AfectacionPresupuestalTableDto>> listByRubro(Long rubroId, PageableDto<?> request);
}
