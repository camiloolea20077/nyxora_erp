package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateRubroPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.RubroPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.RubroPresupuestalTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.UpdateRubroPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface RubroPresupuestalService {
    Mono<RubroPresupuestalResponseDto> create(CreateRubroPresupuestalRequestDto dto);
    Mono<Boolean> update(UpdateRubroPresupuestalRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<RubroPresupuestalResponseDto> findById(Long id);
    Mono<PageResponseDto<RubroPresupuestalTableDto>> list(PageableDto<?> request);
}
