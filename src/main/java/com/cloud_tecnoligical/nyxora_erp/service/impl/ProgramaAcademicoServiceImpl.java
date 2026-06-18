package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateProgramaAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.ProgramaAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.ProgramaAcademicoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateProgramaAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProgramaAcademicoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.academico.ProgramaAcademicoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.ProgramaAcademicoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.ProgramaAcademicoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ProgramaAcademicoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ProgramaAcademicoServiceImpl implements ProgramaAcademicoService {

    private final ProgramaAcademicoR2dbcRepository repo;
    private final ProgramaAcademicoQueryRepository queryRepo;
    private final ProgramaAcademicoMapper mapper;

    public ProgramaAcademicoServiceImpl(ProgramaAcademicoR2dbcRepository repo,
            ProgramaAcademicoQueryRepository queryRepo, ProgramaAcademicoMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<ProgramaAcademicoResponseDto> create(CreateProgramaAcademicoRequestDto dto) {
        return TenantContext.get().flatMap(t -> {
            ProgramaAcademicoEntity e = mapper.toEntity(dto);
            e.setEmpresa_id(t.getEmpresaId());
            e.setActivo(true);
            e.setCreated_at(LocalDateTime.now());
            return repo.save(e).flatMap(saved -> findById(saved.getId()));
        });
    }

    @Override
    public Mono<Boolean> update(UpdateProgramaAcademicoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e -> {
                e.setCodigo(dto.getCodigo());
                e.setNombre(dto.getNombre());
                e.setTipo_programa(dto.getTipoPrograma());
                e.setModalidad(dto.getModalidad());
                e.setCentro_costo_programa_id(dto.getCentroCostoProgramaId());
                e.setCentro_costo_facultad_id(dto.getCentroCostoFacultadId());
                e.setRegistro_academico(dto.getRegistroAcademico());
                e.setDescripcion(dto.getDescripcion());
                if (dto.getActive() != null) e.setActivo(dto.getActive());
                e.setUpdated_at(LocalDateTime.now());
                return repo.save(e).thenReturn(true);
            }));
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
    public Mono<ProgramaAcademicoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Programa no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<ProgramaAcademicoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<ProgramaAcademicoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Programa no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Programa no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
