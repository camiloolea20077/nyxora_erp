package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateGrupoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.GrupoNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.GrupoNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateGrupoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.GrupoNominaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.nomina.GrupoNominaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.GrupoNominaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.GrupoNominaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.GrupoNominaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class GrupoNominaServiceImpl implements GrupoNominaService {

    private final GrupoNominaR2dbcRepository repo;
    private final GrupoNominaQueryRepository queryRepo;
    private final GrupoNominaMapper mapper;

    public GrupoNominaServiceImpl(GrupoNominaR2dbcRepository repo, GrupoNominaQueryRepository queryRepo,
                                  GrupoNominaMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<GrupoNominaResponseDto> create(CreateGrupoNominaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un grupo con ese código"));
                }
                GrupoNominaEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateGrupoNominaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un grupo con ese código"));
                    }
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    e.setFrecuencia_pago(dto.getFrecuenciaPago());
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
    public Mono<GrupoNominaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Grupo de nómina no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<GrupoNominaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<GrupoNominaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Grupo de nómina no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Grupo de nómina no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
