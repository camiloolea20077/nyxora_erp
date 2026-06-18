package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.AddProcesoFaltaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CambiarEstadoProcesoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateProcesoDescargoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateProcesoDisciplinarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateProcesoNotificacionDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDescargoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDisciplinarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDisciplinarioTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoFaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoNotificacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.UpdateProcesoDisciplinarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ProcesoDisciplinarioService {
    Mono<ProcesoDisciplinarioResponseDto> create(CreateProcesoDisciplinarioRequestDto dto);
    Mono<Boolean> update(UpdateProcesoDisciplinarioRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ProcesoDisciplinarioResponseDto> findById(Long id);
    Mono<PageResponseDto<ProcesoDisciplinarioTableDto>> list(PageableDto<?> request);
    Mono<ProcesoDisciplinarioResponseDto> cambiarEstado(Long id, CambiarEstadoProcesoRequestDto dto);

    Mono<List<ProcesoFaltaResponseDto>> listFaltas(Long procesoId);
    Mono<ProcesoFaltaResponseDto> addFalta(Long procesoId, AddProcesoFaltaDto dto);
    Mono<Boolean> removeFalta(Long procesoId, Long id);

    Mono<List<ProcesoDescargoResponseDto>> listDescargos(Long procesoId);
    Mono<ProcesoDescargoResponseDto> addDescargo(Long procesoId, CreateProcesoDescargoDto dto);
    Mono<Boolean> removeDescargo(Long procesoId, Long id);

    Mono<List<ProcesoNotificacionResponseDto>> listNotificaciones(Long procesoId);
    Mono<ProcesoNotificacionResponseDto> addNotificacion(Long procesoId, CreateProcesoNotificacionDto dto);
    Mono<Boolean> removeNotificacion(Long procesoId, Long id);
}
