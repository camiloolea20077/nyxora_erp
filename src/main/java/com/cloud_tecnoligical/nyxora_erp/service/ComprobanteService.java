package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.ComprobanteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.ComprobanteTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateComprobanteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ComprobanteService {
    Mono<ComprobanteResponseDto> create(CreateComprobanteRequestDto dto);
    Mono<ComprobanteResponseDto> findById(Long id);
    Mono<PageResponseDto<ComprobanteTableDto>> list(PageableDto<?> request);
    Mono<Boolean> confirmar(Long id);
    Mono<Boolean> reversar(Long id);
    Mono<Boolean> delete(Long id);

    /** Crea y confirma un comprobante en un solo paso (usado por la interfaz contable de eventos). */
    Mono<ComprobanteResponseDto> crearYConfirmar(CreateComprobanteRequestDto dto);
}
