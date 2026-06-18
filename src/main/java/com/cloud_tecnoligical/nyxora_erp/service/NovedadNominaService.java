package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateNovedadNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.NovedadNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.NovedadNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateNovedadNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface NovedadNominaService {
    Mono<NovedadNominaResponseDto> create(CreateNovedadNominaRequestDto dto);
    Mono<Boolean> update(UpdateNovedadNominaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<NovedadNominaResponseDto> anular(Long id);
    Mono<NovedadNominaResponseDto> findById(Long id);
    Mono<PageResponseDto<NovedadNominaTableDto>> list(PageableDto<?> request, Long vinculacionId);
}
