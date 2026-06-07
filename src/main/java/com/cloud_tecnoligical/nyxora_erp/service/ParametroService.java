package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.CreateParametroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.ParametroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.ParametroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.UpdateParametroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ParametroService {
    Mono<ParametroResponseDto> create(CreateParametroRequestDto dto);
    Mono<Boolean> update(UpdateParametroRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ParametroResponseDto> findById(Long id);
    Mono<ParametroResponseDto> findByClave(String clave);
    Mono<PageResponseDto<ParametroTableDto>> list(PageableDto<?> request);
}
