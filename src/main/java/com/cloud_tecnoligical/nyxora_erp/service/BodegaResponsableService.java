package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponsableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateBodegaResponsableDto;

import reactor.core.publisher.Mono;

public interface BodegaResponsableService {
    Mono<List<BodegaResponsableResponseDto>> list(Long bodegaId);
    Mono<BodegaResponsableResponseDto> create(Long bodegaId, CreateBodegaResponsableDto dto);
    Mono<Boolean> delete(Long bodegaId, Long id);
}
