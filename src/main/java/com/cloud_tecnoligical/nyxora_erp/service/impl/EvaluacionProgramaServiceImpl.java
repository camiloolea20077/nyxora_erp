package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEvaluacionProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionProgramaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionProgramaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEvaluacionProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EvaluacionProgramaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano.EvaluacionProgramaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.talentohumano.EvaluacionProgramaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.talentohumano.EvaluacionProgramaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.EvaluacionProgramaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class EvaluacionProgramaServiceImpl implements EvaluacionProgramaService {

    private final EvaluacionProgramaR2dbcRepository repo;
    private final EvaluacionProgramaQueryRepository queryRepo;
    private final EvaluacionProgramaMapper mapper;

    public EvaluacionProgramaServiceImpl(EvaluacionProgramaR2dbcRepository repo,
                                         EvaluacionProgramaQueryRepository queryRepo,
                                         EvaluacionProgramaMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<EvaluacionProgramaResponseDto> create(CreateEvaluacionProgramaDto dto) {
        return TenantContext.get().flatMap(t ->
            validarCodigoUnico(dto.getCodigo(), null, t.getEmpresaId()).then(Mono.defer(() -> {
                EvaluacionProgramaEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateEvaluacionProgramaDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                validarCodigoUnico(dto.getCodigo(), dto.getId(), t.getEmpresaId()).then(Mono.defer(() -> {
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    e.setFecha_inicial(dto.getFechaInicial());
                    e.setFecha_final(dto.getFechaFinal());
                    if (dto.getActive() != null) e.setActivo(dto.getActive());
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
    public Mono<EvaluacionProgramaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Programa de evaluación no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<EvaluacionProgramaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    /** El código es opcional; solo se valida unicidad cuando viene informado. */
    private Mono<Void> validarCodigoUnico(String codigo, Long idExcluir, Long empresaId) {
        if (codigo == null || codigo.isBlank()) return Mono.empty();
        Mono<Boolean> dup = idExcluir == null
            ? queryRepo.existsByCodigo(codigo, empresaId)
            : queryRepo.existsByCodigoExcludingId(codigo, idExcluir, empresaId);
        return dup.flatMap(existe -> Boolean.TRUE.equals(existe)
            ? Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un programa con ese código"))
            : Mono.empty());
    }

    private Mono<EvaluacionProgramaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Programa de evaluación no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Programa de evaluación no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
