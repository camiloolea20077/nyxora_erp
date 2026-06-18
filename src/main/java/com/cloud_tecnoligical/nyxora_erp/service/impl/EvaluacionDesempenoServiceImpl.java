package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEvaluacionDesempenoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionDesempenoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionDesempenoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEvaluacionDesempenoDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EvaluacionDesempenoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano.EvaluacionDesempenoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.talentohumano.EvaluacionDesempenoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.talentohumano.EvaluacionDesempenoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.talentohumano.EvaluacionProgramaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.EvaluacionDesempenoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class EvaluacionDesempenoServiceImpl implements EvaluacionDesempenoService {

    private final EvaluacionDesempenoR2dbcRepository repo;
    private final EvaluacionDesempenoQueryRepository queryRepo;
    private final EvaluacionDesempenoMapper mapper;
    private final EvaluacionProgramaQueryRepository programaQueryRepo;
    private final TerceroQueryRepository terceroQueryRepository;

    public EvaluacionDesempenoServiceImpl(EvaluacionDesempenoR2dbcRepository repo,
            EvaluacionDesempenoQueryRepository queryRepo, EvaluacionDesempenoMapper mapper,
            EvaluacionProgramaQueryRepository programaQueryRepo, TerceroQueryRepository terceroQueryRepository) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
        this.programaQueryRepo = programaQueryRepo;
        this.terceroQueryRepository = terceroQueryRepository;
    }

    @Override
    public Mono<EvaluacionDesempenoResponseDto> create(CreateEvaluacionDesempenoDto dto) {
        return TenantContext.get().flatMap(t ->
            validarReferencias(dto.getEvaluacionProgramaId(), dto.getEmpleadoId(), t.getEmpresaId()).then(Mono.defer(() -> {
                EvaluacionDesempenoEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateEvaluacionDesempenoDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                validarReferencias(dto.getEvaluacionProgramaId(), dto.getEmpleadoId(), t.getEmpresaId()).then(Mono.defer(() -> {
                    mapper.updateEntityFromDto(dto, e);
                    e.setUpdated_at(LocalDateTime.now());
                    return repo.save(e).thenReturn(true);
                }))));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setDeleted_at(LocalDateTime.now());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<EvaluacionDesempenoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Evaluación no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<EvaluacionDesempenoTableDto>> list(PageableDto<?> request, Long empleadoId, Long programaId) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId(), empleadoId, programaId));
    }

    /** Valida que el programa (si viene) y el empleado existan en la empresa. */
    private Mono<Void> validarReferencias(Long programaId, Long empleadoId, Long empresaId) {
        Mono<Void> validaPrograma = programaId == null ? Mono.empty()
            : programaQueryRepo.existsActivoEnEmpresa(programaId, empresaId)
                .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Programa de evaluación no encontrado")));
        Mono<Void> validaEmpleado = empleadoId == null ? Mono.empty()
            : terceroQueryRepository.existsActivoEnEmpresa(empleadoId, empresaId)
                .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Empleado no encontrado")));
        return validaPrograma.then(validaEmpleado);
    }

    private Mono<EvaluacionDesempenoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Evaluación no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Evaluación no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
