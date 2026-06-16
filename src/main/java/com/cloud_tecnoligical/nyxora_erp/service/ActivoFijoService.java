package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.AsignarPolizaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.AsignarResponsableRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreateActivoFijoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.UpdateActivoFijoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ActivoFijoService {
    Mono<ActivoFijoResponseDto> create(CreateActivoFijoRequestDto dto);
    Mono<Boolean> update(UpdateActivoFijoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ActivoFijoResponseDto> findById(Long id);
    Mono<PageResponseDto<ActivoFijoTableDto>> list(PageableDto<?> request);

    Mono<ActivoFijoResponseDto> asignarResponsable(Long activoFijoId, AsignarResponsableRequestDto dto);
    Mono<Boolean> removerResponsable(Long activoFijoId, Long terceroId);
    Mono<ActivoFijoResponseDto> asignarPoliza(Long activoFijoId, AsignarPolizaRequestDto dto);
    Mono<Boolean> removerPoliza(Long activoFijoId, Long polizaSeguroId);
}
