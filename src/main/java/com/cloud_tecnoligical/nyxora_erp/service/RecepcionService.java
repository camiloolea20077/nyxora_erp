package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.ConfirmarRecepcionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateRecepcionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface RecepcionService {
    Mono<RecepcionResponseDto> create(CreateRecepcionRequestDto dto);
    Mono<RecepcionResponseDto> findById(Long id);
    Mono<PageResponseDto<RecepcionTableDto>> list(PageableDto<?> request);
    Mono<RecepcionResponseDto> confirmar(Long id, ConfirmarRecepcionRequestDto params);
}
