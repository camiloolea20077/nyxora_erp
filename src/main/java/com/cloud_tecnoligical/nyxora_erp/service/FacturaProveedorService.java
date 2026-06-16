package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.CreateFacturaProveedorRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.RegistrarEventoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.UpdateFacturaProveedorRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface FacturaProveedorService {
    Mono<FacturaProveedorResponseDto> create(CreateFacturaProveedorRequestDto dto);
    Mono<Boolean> update(UpdateFacturaProveedorRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<FacturaProveedorResponseDto> findById(Long id);
    Mono<PageResponseDto<FacturaProveedorTableDto>> list(PageableDto<?> request);
    Mono<FacturaProveedorResponseDto> registrarEvento(Long id, RegistrarEventoRequestDto dto);
}
