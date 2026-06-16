package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ComprobanteEgresoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ComprobanteEgresoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateComprobanteEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.GirarEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.UpdateComprobanteEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ComprobanteEgresoService {
    Mono<ComprobanteEgresoResponseDto> create(CreateComprobanteEgresoRequestDto dto);
    Mono<Boolean> update(UpdateComprobanteEgresoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ComprobanteEgresoResponseDto> findById(Long id);
    Mono<PageResponseDto<ComprobanteEgresoTableDto>> list(PageableDto<?> request);
    Mono<ComprobanteEgresoResponseDto> girar(Long id, GirarEgresoRequestDto params);
    Mono<Boolean> anular(Long id);
}
