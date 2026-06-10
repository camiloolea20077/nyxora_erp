package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.adjunto.AdjuntoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.adjunto.CreateAdjuntoRequestDto;

import reactor.core.publisher.Mono;

public interface AdjuntoService {
    Mono<AdjuntoResponseDto> create(CreateAdjuntoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<AdjuntoResponseDto> findById(Long id);
    Mono<List<AdjuntoResponseDto>> listByObjeto(String modulo, String entidad, Long entidadId);
}
