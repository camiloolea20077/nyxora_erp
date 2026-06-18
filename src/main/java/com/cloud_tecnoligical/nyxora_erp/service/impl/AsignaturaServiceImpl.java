package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaProgramaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateAsignaturaProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateAsignaturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateAsignaturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.AsignaturaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.AsignaturaProgramaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.academico.AsignaturaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.AsignaturaProgramaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.AsignaturaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.AsignaturaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.ProgramaAcademicoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.AsignaturaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class AsignaturaServiceImpl implements AsignaturaService {

    private final AsignaturaR2dbcRepository repo;
    private final AsignaturaProgramaR2dbcRepository enlaceRepo;
    private final AsignaturaQueryRepository queryRepo;
    private final ProgramaAcademicoQueryRepository programaQueryRepository;
    private final AsignaturaMapper mapper;

    public AsignaturaServiceImpl(AsignaturaR2dbcRepository repo, AsignaturaProgramaR2dbcRepository enlaceRepo,
            AsignaturaQueryRepository queryRepo, ProgramaAcademicoQueryRepository programaQueryRepository,
            AsignaturaMapper mapper) {
        this.repo = repo;
        this.enlaceRepo = enlaceRepo;
        this.queryRepo = queryRepo;
        this.programaQueryRepository = programaQueryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<AsignaturaResponseDto> create(CreateAsignaturaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una asignatura con ese código"));
                }
                AsignaturaEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateAsignaturaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una asignatura con ese código"));
                    }
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    e.setDescripcion(dto.getDescripcion());
                    e.setCentro_costo_departamento_id(dto.getCentroCostoDepartamentoId());
                    if (dto.getActive() != null) e.setActivo(dto.getActive());
                    e.setUpdated_at(LocalDateTime.now());
                    return repo.save(e).thenReturn(true);
                })));
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
    public Mono<AsignaturaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Asignatura no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<AsignaturaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    // ---------- Satélite: programas ----------
    @Override
    public Mono<List<AsignaturaProgramaResponseDto>> listProgramas(Long asignaturaId) {
        return validarAsignatura(asignaturaId).then(queryRepo.listProgramas(asignaturaId));
    }

    @Override
    public Mono<AsignaturaProgramaResponseDto> addPrograma(Long asignaturaId, CreateAsignaturaProgramaDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(asignaturaId, t.getEmpresaId())
                .then(programaQueryRepository.existsActivoEnEmpresa(dto.getProgramaAcademicoId(), t.getEmpresaId()))
                .flatMap(ok -> {
                    if (!Boolean.TRUE.equals(ok)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El programa no existe"));
                    }
                    return queryRepo.existeEnlaceVigente(asignaturaId, dto.getProgramaAcademicoId()).flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La asignatura ya está en ese programa"));
                        }
                        AsignaturaProgramaEntity e = new AsignaturaProgramaEntity();
                        e.setAsignatura_id(asignaturaId);
                        e.setPrograma_academico_id(dto.getProgramaAcademicoId());
                        e.setSemestre(dto.getSemestre());
                        e.setCreditos(dto.getCreditos());
                        e.setActivo(true);
                        e.setCreated_at(LocalDateTime.now());
                        return enlaceRepo.save(e).then(queryRepo.listProgramas(asignaturaId))
                            .map(list -> list.stream().filter(x -> x.getProgramaAcademicoId().equals(dto.getProgramaAcademicoId()))
                                .findFirst().orElse(null));
                    });
                }));
    }

    @Override
    public Mono<Boolean> removePrograma(Long asignaturaId, Long enlaceId) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(asignaturaId, t.getEmpresaId()).then(enlaceRepo.findById(enlaceId)
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Enlace no encontrado")))
                .flatMap(e -> {
                    if (e.getDeleted_at() != null || !e.getAsignatura_id().equals(asignaturaId)) {
                        return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Enlace no encontrado"));
                    }
                    e.setDeleted_at(LocalDateTime.now());
                    return enlaceRepo.save(e).thenReturn(true);
                })));
    }

    private Mono<Void> validarAsignatura(Long asignaturaId) {
        return TenantContext.get().flatMap(t -> cargarEntidad(asignaturaId, t.getEmpresaId()).then());
    }

    private Mono<AsignaturaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Asignatura no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Asignatura no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
