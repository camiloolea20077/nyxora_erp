package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoEstudioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoFamiliarDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoHistoriaLaboralDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoEstudioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoFamiliarResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoHistoriaLaboralResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoEstudioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoFamiliarDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoHistoriaLaboralDto;

import reactor.core.publisher.Mono;

public interface EmpleadoSatelitesService {

    // Estudios
    Mono<List<EmpleadoEstudioResponseDto>> listEstudios(Long empleadoId);
    Mono<EmpleadoEstudioResponseDto> createEstudio(Long empleadoId, CreateEmpleadoEstudioDto dto);
    Mono<Boolean> updateEstudio(Long empleadoId, UpdateEmpleadoEstudioDto dto);
    Mono<Boolean> deleteEstudio(Long empleadoId, Long id);

    // Familiares
    Mono<List<EmpleadoFamiliarResponseDto>> listFamiliares(Long empleadoId);
    Mono<EmpleadoFamiliarResponseDto> createFamiliar(Long empleadoId, CreateEmpleadoFamiliarDto dto);
    Mono<Boolean> updateFamiliar(Long empleadoId, UpdateEmpleadoFamiliarDto dto);
    Mono<Boolean> deleteFamiliar(Long empleadoId, Long id);

    // Historia laboral
    Mono<List<EmpleadoHistoriaLaboralResponseDto>> listHistorias(Long empleadoId);
    Mono<EmpleadoHistoriaLaboralResponseDto> createHistoria(Long empleadoId, CreateEmpleadoHistoriaLaboralDto dto);
    Mono<Boolean> updateHistoria(Long empleadoId, UpdateEmpleadoHistoriaLaboralDto dto);
    Mono<Boolean> deleteHistoria(Long empleadoId, Long id);
}
