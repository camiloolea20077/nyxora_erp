package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CreateCuentaPorCobrarRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CuentaPorCobrarResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CuentaPorCobrarTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CuentaPorCobrarEntity;
import com.cloud_tecnoligical.nyxora_erp.event.CuentaPorCobrarSolicitada;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface CuentaPorCobrarService {
    Mono<CuentaPorCobrarResponseDto> create(CreateCuentaPorCobrarRequestDto dto);
    Mono<CuentaPorCobrarResponseDto> findById(Long id);
    Mono<PageResponseDto<CuentaPorCobrarTableDto>> list(PageableDto<?> request);

    /** Crea la CxC a partir del evento de Facturación (consistencia eventual). Sin TenantContext. */
    Mono<CuentaPorCobrarEntity> crearDesdeEvento(CuentaPorCobrarSolicitada ev);
}
