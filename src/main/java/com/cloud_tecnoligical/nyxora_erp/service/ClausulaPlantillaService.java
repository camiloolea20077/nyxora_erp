package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ClausulaPlantillaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ClausulaPlantillaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateClausulaPlantillaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.UpdateClausulaPlantillaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ClausulaPlantillaService {
    Mono<ClausulaPlantillaResponseDto> create(CreateClausulaPlantillaRequestDto dto);
    Mono<Boolean> update(UpdateClausulaPlantillaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ClausulaPlantillaResponseDto> findById(Long id);
    Mono<PageResponseDto<ClausulaPlantillaTableDto>> list(PageableDto<?> request);
}
