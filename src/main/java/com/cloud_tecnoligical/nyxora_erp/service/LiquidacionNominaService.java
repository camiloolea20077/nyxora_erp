package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.AportePilaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ContabilizarNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateLiquidacionNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionDetalleDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidarNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateLiquidacionNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface LiquidacionNominaService {
    Mono<LiquidacionNominaResponseDto> create(CreateLiquidacionNominaRequestDto dto);
    Mono<Boolean> update(UpdateLiquidacionNominaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<LiquidacionNominaResponseDto> findById(Long id);
    Mono<PageResponseDto<LiquidacionNominaTableDto>> list(PageableDto<?> request, Long grupoNominaId);

    Mono<LiquidacionNominaResponseDto> liquidar(Long id, LiquidarNominaRequestDto dto);
    Mono<LiquidacionNominaResponseDto> contabilizar(Long id, ContabilizarNominaRequestDto dto);
    Mono<LiquidacionNominaResponseDto> anular(Long id);

    Mono<List<LiquidacionDetalleDto>> listDetalle(Long id);
    Mono<List<AportePilaDto>> listPila(Long id);
}
