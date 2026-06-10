package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateCuentaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CuentaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CuentaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.UpdateCuentaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface CuentaService {
    Mono<CuentaResponseDto> create(CreateCuentaRequestDto dto);
    Mono<Boolean> update(UpdateCuentaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<CuentaResponseDto> findById(Long id);
    Mono<PageResponseDto<CuentaTableDto>> list(PageableDto<?> request);
}
