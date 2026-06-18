package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateGrupoAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GrupoAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GrupoAcademicoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateGrupoAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface GrupoAcademicoService {
    Mono<GrupoAcademicoResponseDto> create(CreateGrupoAcademicoRequestDto dto);
    Mono<Boolean> update(UpdateGrupoAcademicoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<GrupoAcademicoResponseDto> findById(Long id);
    Mono<PageResponseDto<GrupoAcademicoTableDto>> list(PageableDto<?> request);
}
