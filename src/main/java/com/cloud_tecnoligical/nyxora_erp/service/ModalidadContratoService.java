package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateModalidadContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ModalidadContratoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ModalidadContratoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.UpdateModalidadContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ModalidadContratoService {
    Mono<ModalidadContratoResponseDto> create(CreateModalidadContratoRequestDto dto);
    Mono<Boolean> update(UpdateModalidadContratoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ModalidadContratoResponseDto> findById(Long id);
    Mono<PageResponseDto<ModalidadContratoTableDto>> list(PageableDto<?> request);
}
