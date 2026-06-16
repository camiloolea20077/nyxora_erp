package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.AsignarPolizaContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CambiarEstadoContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.UpdateContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ContratoService {
    Mono<ContratoResponseDto> create(CreateContratoRequestDto dto);
    Mono<Boolean> update(UpdateContratoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ContratoResponseDto> findById(Long id);
    Mono<PageResponseDto<ContratoTableDto>> list(PageableDto<?> request);

    Mono<ContratoResponseDto> cambiarEstado(Long id, CambiarEstadoContratoRequestDto dto);
    Mono<ContratoResponseDto> asignarPoliza(Long id, AsignarPolizaContratoRequestDto dto);
    Mono<Boolean> removerPoliza(Long id, Long polizaSeguroId);
}
