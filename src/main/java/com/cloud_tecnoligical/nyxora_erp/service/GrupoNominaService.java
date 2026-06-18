package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateGrupoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.GrupoNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.GrupoNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateGrupoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface GrupoNominaService {
    Mono<GrupoNominaResponseDto> create(CreateGrupoNominaRequestDto dto);
    Mono<Boolean> update(UpdateGrupoNominaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<GrupoNominaResponseDto> findById(Long id);
    Mono<PageResponseDto<GrupoNominaTableDto>> list(PageableDto<?> request);
}
