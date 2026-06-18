package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateVinculacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateVinculacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.VinculacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.VinculacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface VinculacionService {
    Mono<VinculacionResponseDto> create(CreateVinculacionRequestDto dto);
    Mono<Boolean> update(UpdateVinculacionRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<VinculacionResponseDto> findById(Long id);
    Mono<PageResponseDto<VinculacionTableDto>> list(PageableDto<?> request, Long empleadoId);
}
