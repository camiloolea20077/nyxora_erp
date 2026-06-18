package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaProgramaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateAsignaturaProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateAsignaturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateAsignaturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface AsignaturaService {
    Mono<AsignaturaResponseDto> create(CreateAsignaturaRequestDto dto);
    Mono<Boolean> update(UpdateAsignaturaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<AsignaturaResponseDto> findById(Long id);
    Mono<PageResponseDto<AsignaturaTableDto>> list(PageableDto<?> request);

    Mono<List<AsignaturaProgramaResponseDto>> listProgramas(Long asignaturaId);
    Mono<AsignaturaProgramaResponseDto> addPrograma(Long asignaturaId, CreateAsignaturaProgramaDto dto);
    Mono<Boolean> removePrograma(Long asignaturaId, Long enlaceId);
}
