package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaAcademicaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaAcademicaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateCargaAcademicaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GenerarNovedadDocenteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateCargaAcademicaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface CargaAcademicaService {
    Mono<CargaAcademicaResponseDto> create(CreateCargaAcademicaRequestDto dto);
    Mono<Boolean> update(UpdateCargaAcademicaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<CargaAcademicaResponseDto> findById(Long id);
    Mono<PageResponseDto<CargaAcademicaTableDto>> list(PageableDto<?> request, Long vinculacionId);
    Mono<CargaAcademicaResponseDto> generarNovedad(Long id, GenerarNovedadDocenteRequestDto dto);
}
