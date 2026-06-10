package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateUbicacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UbicacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UbicacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateUbicacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface UbicacionService {
    Mono<UbicacionResponseDto> create(CreateUbicacionRequestDto dto);
    Mono<Boolean> update(UpdateUbicacionRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<UbicacionResponseDto> findById(Long id);
    Mono<PageResponseDto<UbicacionTableDto>> list(PageableDto<?> request, Long bodegaId);
}
