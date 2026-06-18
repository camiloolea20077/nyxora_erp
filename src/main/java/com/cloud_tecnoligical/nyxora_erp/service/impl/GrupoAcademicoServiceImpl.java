package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateGrupoAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GrupoAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GrupoAcademicoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateGrupoAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.GrupoAcademicoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.academico.GrupoAcademicoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.GrupoAcademicoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.GrupoAcademicoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.GrupoAcademicoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class GrupoAcademicoServiceImpl implements GrupoAcademicoService {

    private final GrupoAcademicoR2dbcRepository repo;
    private final GrupoAcademicoQueryRepository queryRepo;
    private final GrupoAcademicoMapper mapper;

    public GrupoAcademicoServiceImpl(GrupoAcademicoR2dbcRepository repo, GrupoAcademicoQueryRepository queryRepo,
            GrupoAcademicoMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<GrupoAcademicoResponseDto> create(CreateGrupoAcademicoRequestDto dto) {
        return TenantContext.get().flatMap(t -> {
            GrupoAcademicoEntity e = mapper.toEntity(dto);
            e.setEmpresa_id(t.getEmpresaId());
            e.setActivo(true);
            e.setCreated_at(LocalDateTime.now());
            return repo.save(e).flatMap(saved -> findById(saved.getId()));
        });
    }

    @Override
    public Mono<Boolean> update(UpdateGrupoAcademicoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e -> {
                e.setPrograma_academico_id(dto.getProgramaAcademicoId());
                e.setCodigo(dto.getCodigo());
                e.setNombre(dto.getNombre());
                e.setPeriodo(dto.getPeriodo());
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
    public Mono<GrupoAcademicoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Grupo académico no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<GrupoAcademicoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<GrupoAcademicoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Grupo académico no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Grupo académico no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
