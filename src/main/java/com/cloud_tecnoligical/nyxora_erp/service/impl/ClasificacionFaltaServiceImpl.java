package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ClasificacionFaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ClasificacionFaltaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateClasificacionFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.UpdateClasificacionFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ClasificacionFaltaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.juridico.ClasificacionFaltaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.ClasificacionFaltaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.ClasificacionFaltaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ClasificacionFaltaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ClasificacionFaltaServiceImpl implements ClasificacionFaltaService {

    private final ClasificacionFaltaR2dbcRepository repo;
    private final ClasificacionFaltaQueryRepository queryRepo;
    private final ClasificacionFaltaMapper mapper;

    public ClasificacionFaltaServiceImpl(ClasificacionFaltaR2dbcRepository repo,
            ClasificacionFaltaQueryRepository queryRepo, ClasificacionFaltaMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<ClasificacionFaltaResponseDto> create(CreateClasificacionFaltaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una clasificación con ese código"));
                }
                ClasificacionFaltaEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateClasificacionFaltaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una clasificación con ese código"));
                    }
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    if (dto.getActive() != null) e.setActivo(dto.getActive());
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
    public Mono<ClasificacionFaltaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Clasificación no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<ClasificacionFaltaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<ClasificacionFaltaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Clasificación no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Clasificación no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
