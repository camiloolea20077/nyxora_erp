package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreatePolizaSeguroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.PolizaSeguroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.PolizaSeguroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.UpdatePolizaSeguroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.PolizaSeguroEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.activosfijos.PolizaSeguroMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.activosfijos.PolizaSeguroQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.activosfijos.PolizaSeguroR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.PolizaSeguroService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class PolizaSeguroServiceImpl implements PolizaSeguroService {

    private final PolizaSeguroR2dbcRepository repo;
    private final PolizaSeguroQueryRepository queryRepo;
    private final PolizaSeguroMapper mapper;

    public PolizaSeguroServiceImpl(PolizaSeguroR2dbcRepository repo,
                                   PolizaSeguroQueryRepository queryRepo,
                                   PolizaSeguroMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<PolizaSeguroResponseDto> create(CreatePolizaSeguroRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByNumero(dto.getNumero(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una póliza con ese número"));
                }
                PolizaSeguroEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdatePolizaSeguroRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByNumeroExcludingId(dto.getNumero(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una póliza con ese número"));
                    }
                    e.setNumero(dto.getNumero());
                    e.setAseguradora_id(dto.getAseguradoraId());
                    e.setTipo(dto.getTipo());
                    e.setFecha_inicio(dto.getFechaInicio());
                    e.setFecha_fin(dto.getFechaFin());
                    e.setValor_asegurado(dto.getValorAsegurado());
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
    public Mono<PolizaSeguroResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Póliza no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<PolizaSeguroTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<PolizaSeguroEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Póliza no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Póliza no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
