package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.CreateRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.RolResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.RolTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.UpdateRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.RolEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.rol.RolMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.rol.RolQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.rol.RolR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.RolService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class RolServiceImpl implements RolService {

    private final RolR2dbcRepository rolR2dbcRepository;
    private final RolQueryRepository rolQueryRepository;
    private final RolMapper rolMapper;

    public RolServiceImpl(RolR2dbcRepository rolR2dbcRepository,
                          RolQueryRepository rolQueryRepository, RolMapper rolMapper) {
        this.rolR2dbcRepository = rolR2dbcRepository;
        this.rolQueryRepository = rolQueryRepository;
        this.rolMapper = rolMapper;
    }

    @Override
    public Mono<RolResponseDto> create(CreateRolRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            rolQueryRepository.existsActiveByNombre(dto.getName(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un rol con ese nombre"));
                    }
                    RolEntity entity = rolMapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setActivo(true);
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setCreated_at(LocalDateTime.now());
                    return rolR2dbcRepository.save(entity)
                        .flatMap(saved -> rolQueryRepository.setPermisos(saved.getId(), dto.getPermisoIds())
                            .then(Mono.fromCallable(() -> {
                                RolResponseDto resp = rolMapper.toResponseDto(saved);
                                resp.setPermisoIds(dto.getPermisoIds());
                                return resp;
                            })));
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateRolRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            rolR2dbcRepository.findById(dto.getId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Rol no encontrado")))
                .flatMap(entity -> validarTenant(entity, t.getEmpresaId())
                    .then(rolQueryRepository.existsActiveByNombreExcludingId(dto.getName(), dto.getId(), t.getEmpresaId()))
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otro rol con ese nombre"));
                        }
                        entity.setNombre(dto.getName());
                        entity.setUsuario_modificacion(t.getUsuarioId());
                        entity.setUpdated_at(LocalDateTime.now());
                        Mono<Void> permisos = dto.getPermisoIds() != null
                            ? rolQueryRepository.setPermisos(dto.getId(), dto.getPermisoIds())
                            : Mono.empty();
                        return rolR2dbcRepository.save(entity).then(permisos).thenReturn(true);
                    })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            rolR2dbcRepository.findById(id)
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Rol no encontrado")))
                .flatMap(entity -> validarTenant(entity, t.getEmpresaId()).then(Mono.defer(() -> {
                    entity.setDeleted_at(LocalDateTime.now());
                    entity.setUsuario_modificacion(t.getUsuarioId());
                    return rolR2dbcRepository.save(entity).thenReturn(true);
                }))));
    }

    @Override
    public Mono<RolResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            rolQueryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Rol no encontrado")))
                .flatMap(resp -> rolQueryRepository.findPermisoIds(resp.getId())
                    .map(ids -> { resp.setPermisoIds(ids); return resp; })));
    }

    @Override
    public Mono<PageResponseDto<RolTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> rolQueryRepository.listRoles(request, t.getEmpresaId()));
    }

    private Mono<Void> validarTenant(RolEntity entity, Long empresaId) {
        if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
            return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
        }
        return Mono.empty();
    }
}
