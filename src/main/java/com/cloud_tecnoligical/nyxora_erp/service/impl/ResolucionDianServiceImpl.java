package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.CreateResolucionDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.ResolucionDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.ResolucionDianTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.UpdateResolucionDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ResolucionDianEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.facturacion.ResolucionDianMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.facturacion.ResolucionDianQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.facturacion.ResolucionDianR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ResolucionDianService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ResolucionDianServiceImpl implements ResolucionDianService {

    private final ResolucionDianR2dbcRepository repo;
    private final ResolucionDianQueryRepository queryRepo;
    private final ResolucionDianMapper mapper;

    public ResolucionDianServiceImpl(ResolucionDianR2dbcRepository repo, ResolucionDianQueryRepository queryRepo,
                                     ResolucionDianMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<ResolucionDianResponseDto> create(CreateResolucionDianRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByNumero(dto.getNumeroResolucion(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una resolución con ese número"));
                }
                ResolucionDianEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setConsecutivo_actual(0L);
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateResolucionDianRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByNumeroExcludingId(dto.getNumeroResolucion(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una resolución con ese número"));
                    }
                    e.setNumero_resolucion(dto.getNumeroResolucion());
                    e.setPrefijo(dto.getPrefijo());
                    e.setFactura_inicial(dto.getFacturaInicial());
                    e.setFactura_final(dto.getFacturaFinal());
                    e.setFecha_inicial(dto.getFechaInicial());
                    e.setFecha_final(dto.getFechaFinal());
                    e.setClave_tecnica(dto.getClaveTecnica());
                    e.setDescripcion(dto.getDescripcion());
                    e.setUsuario_modificacion(t.getUsuarioId());
                    e.setUpdated_at(LocalDateTime.now());
                    return repo.save(e).thenReturn(true);
                })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setDeleted_at(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<ResolucionDianResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Resolución no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<ResolucionDianTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<ResolucionDianEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Resolución no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Resolución no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
