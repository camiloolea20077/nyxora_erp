package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ConceptoNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ConceptoNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateConceptoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateConceptoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ConceptoNominaService {
    Mono<ConceptoNominaResponseDto> create(CreateConceptoNominaRequestDto dto);
    Mono<Boolean> update(UpdateConceptoNominaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ConceptoNominaResponseDto> findById(Long id);
    Mono<PageResponseDto<ConceptoNominaTableDto>> list(PageableDto<?> request, String clase);
}
