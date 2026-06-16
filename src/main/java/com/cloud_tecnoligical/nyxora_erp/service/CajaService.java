package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.AbrirCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CajaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CajaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.UpdateCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface CajaService {
    Mono<CajaResponseDto> create(CreateCajaRequestDto dto);
    Mono<Boolean> update(UpdateCajaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<CajaResponseDto> findById(Long id);
    Mono<PageResponseDto<CajaTableDto>> list(PageableDto<?> request);
    Mono<CajaResponseDto> abrir(Long id, AbrirCajaRequestDto dto);
    Mono<CajaResponseDto> cerrar(Long id);
}
