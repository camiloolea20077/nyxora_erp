package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CpcResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CpcTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateCpcRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.UpdateCpcRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CpcEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.presupuesto.CpcMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.CpcQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.CpcR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CpcService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class CpcServiceImpl implements CpcService {

    private final CpcR2dbcRepository repo;
    private final CpcQueryRepository queryRepo;
    private final CpcMapper mapper;

    public CpcServiceImpl(CpcR2dbcRepository repo, CpcQueryRepository queryRepo, CpcMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<CpcResponseDto> create(CreateCpcRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un CPC con ese código"));
                }
                CpcEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                if (e.getManeja_movimiento() == null) e.setManeja_movimiento(false);
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateCpcRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un CPC con ese código"));
                    }
                    e.setVigencia_id(dto.getVigenciaId());
                    e.setCpc_padre_id(dto.getCpcPadreId());
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    if (dto.getManejaMovimiento() != null) e.setManeja_movimiento(dto.getManejaMovimiento());
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
    public Mono<CpcResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "CPC no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<CpcTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<CpcEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "CPC no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "CPC no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
