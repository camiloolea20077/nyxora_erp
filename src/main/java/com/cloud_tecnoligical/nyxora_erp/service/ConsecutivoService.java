package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.documento.ConsecutivoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.ConsecutivoResponseDto;

import reactor.core.publisher.Mono;

public interface ConsecutivoService {

    /** Devuelve el siguiente número (atómico) para el tipo de documento dado. */
    Mono<ConsecutivoResponseDto> siguiente(Long tipoDocumentoId, ConsecutivoRequestDto request);
}
