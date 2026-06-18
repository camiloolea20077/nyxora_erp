package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateProgramaAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.ProgramaAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.ProgramaAcademicoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateProgramaAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ProgramaAcademicoService {
    Mono<ProgramaAcademicoResponseDto> create(CreateProgramaAcademicoRequestDto dto);
    Mono<Boolean> update(UpdateProgramaAcademicoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ProgramaAcademicoResponseDto> findById(Long id);
    Mono<PageResponseDto<ProgramaAcademicoTableDto>> list(PageableDto<?> request);
}
