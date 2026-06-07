package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.CreateSedeRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.SedeResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.SedeTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.UpdateSedeRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.SedeEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.sede.SedeMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.sede.SedeQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.sede.SedeR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.SedeService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class SedeServiceImpl implements SedeService {

    private final SedeR2dbcRepository sedeR2dbcRepository;
    private final SedeQueryRepository sedeQueryRepository;
    private final SedeMapper sedeMapper;

    public SedeServiceImpl(SedeR2dbcRepository sedeR2dbcRepository,
                           SedeQueryRepository sedeQueryRepository, SedeMapper sedeMapper) {
        this.sedeR2dbcRepository = sedeR2dbcRepository;
        this.sedeQueryRepository = sedeQueryRepository;
        this.sedeMapper = sedeMapper;
    }

    @Override
    public Mono<SedeResponseDto> create(CreateSedeRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            sedeQueryRepository.existsActiveByCodigo(dto.getCode(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una sede con ese código"));
                    }
                    SedeEntity entity = sedeMapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    return sedeR2dbcRepository.save(entity).map(sedeMapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateSedeRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            sedeR2dbcRepository.findById(dto.getId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Sede no encontrada")))
                .flatMap(entity -> validarTenant(entity, t.getEmpresaId())
                    .then(sedeQueryRepository.existsActiveByCodigoExcludingId(dto.getCode(), dto.getId(), t.getEmpresaId()))
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otra sede con ese código"));
                        }
                        sedeMapper.updateEntityFromDto(dto, entity);
                        entity.setUsuario_modificacion(t.getUsuarioId());
                        entity.setUpdated_at(LocalDateTime.now());
                        return sedeR2dbcRepository.save(entity).thenReturn(true);
                    })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            sedeR2dbcRepository.findById(id)
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Sede no encontrada")))
                .flatMap(entity -> validarTenant(entity, t.getEmpresaId()).then(Mono.defer(() -> {
                    entity.setDeleted_at(LocalDateTime.now());
                    entity.setUsuario_modificacion(t.getUsuarioId());
                    return sedeR2dbcRepository.save(entity).thenReturn(true);
                }))));
    }

    @Override
    public Mono<SedeResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            sedeQueryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Sede no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<SedeTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> sedeQueryRepository.listSedes(request, t.getEmpresaId()));
    }

    // Cross-tenant + soft-delete → 404 con el mismo mensaje (no filtrar existencia)
    private Mono<Void> validarTenant(SedeEntity entity, Long empresaId) {
        if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
            return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Sede no encontrada"));
        }
        return Mono.empty();
    }
}
