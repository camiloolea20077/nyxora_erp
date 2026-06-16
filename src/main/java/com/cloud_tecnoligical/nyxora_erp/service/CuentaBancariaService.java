package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateCuentaBancariaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CuentaBancariaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.UpdateCuentaBancariaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface CuentaBancariaService {
    Mono<CuentaBancariaResponseDto> create(CreateCuentaBancariaRequestDto dto);
    Mono<Boolean> update(UpdateCuentaBancariaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<CuentaBancariaResponseDto> findById(Long id);
    Mono<PageResponseDto<CuentaBancariaTableDto>> list(PageableDto<?> request);
}
