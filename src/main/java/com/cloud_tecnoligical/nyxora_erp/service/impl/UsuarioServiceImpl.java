package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.AsignarRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.CreateUsuarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UpdateUsuarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UsuarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UsuarioTableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.UsuarioEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.usuario.UsuarioMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.auth.UsuarioR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.usuario.UsuarioQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.UsuarioService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioR2dbcRepository usuarioR2dbcRepository;
    private final UsuarioQueryRepository usuarioQueryRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioR2dbcRepository usuarioR2dbcRepository,
                              UsuarioQueryRepository usuarioQueryRepository,
                              UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioR2dbcRepository = usuarioR2dbcRepository;
        this.usuarioQueryRepository = usuarioQueryRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UsuarioResponseDto> create(CreateUsuarioRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            // 1) el tercero (persona) debe existir y ser de la empresa
            usuarioQueryRepository.existsTerceroValido(dto.getTerceroId(), t.getEmpresaId())
                .flatMap(terceroOk -> {
                    if (Boolean.FALSE.equals(terceroOk)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El tercero indicado no existe"));
                    }
                    // 2) ese tercero no puede tener ya un usuario activo
                    return usuarioQueryRepository.existsUsuarioActivoByTercero(dto.getTerceroId(), t.getEmpresaId());
                })
                .flatMap(terceroTieneUsuario -> {
                    if (Boolean.TRUE.equals(terceroTieneUsuario)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El tercero ya tiene un usuario asignado"));
                    }
                    return usuarioQueryRepository.existsActiveByUsername(dto.getUsername(), t.getEmpresaId());
                })
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese nombre"));
                    }
                    return Mono.fromCallable(() -> passwordEncoder.encode(dto.getPassword()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(hash -> {
                            UsuarioEntity entity = usuarioMapper.toEntity(dto);
                            entity.setEmpresa_id(t.getEmpresaId());
                            entity.setHash_password(hash);
                            entity.setActivo(true);
                            entity.setUsuario_creacion(t.getUsuarioId());
                            entity.setCreated_at(LocalDateTime.now());
                            return usuarioR2dbcRepository.save(entity).map(usuarioMapper::toResponseDto);
                        });
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateUsuarioRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            usuarioR2dbcRepository.findById(dto.getId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado")))
                .flatMap(entity -> validarTenant(entity, t.getEmpresaId()).then(Mono.defer(() -> {
                    entity.setEmail(dto.getEmail());
                    if (dto.getActive() != null) {
                        entity.setActivo(dto.getActive());
                    }
                    entity.setUsuario_modificacion(t.getUsuarioId());
                    entity.setUpdated_at(LocalDateTime.now());
                    Mono<String> hashMono = (dto.getPassword() != null && !dto.getPassword().isBlank())
                        ? Mono.fromCallable(() -> passwordEncoder.encode(dto.getPassword())).subscribeOn(Schedulers.boundedElastic())
                        : Mono.just(entity.getHash_password());
                    return hashMono.flatMap(hash -> {
                        entity.setHash_password(hash);
                        return usuarioR2dbcRepository.save(entity).thenReturn(true);
                    });
                }))));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            usuarioR2dbcRepository.findById(id)
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado")))
                .flatMap(entity -> validarTenant(entity, t.getEmpresaId()).then(Mono.defer(() -> {
                    entity.setDeleted_at(LocalDateTime.now());
                    entity.setUsuario_modificacion(t.getUsuarioId());
                    return usuarioR2dbcRepository.save(entity).thenReturn(true);
                }))));
    }

    @Override
    public Mono<UsuarioResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            usuarioQueryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<UsuarioTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> usuarioQueryRepository.listUsuarios(request, t.getEmpresaId()));
    }

    @Override
    public Mono<Boolean> asignarRol(Long usuarioId, AsignarRolRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            usuarioR2dbcRepository.findById(usuarioId)
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado")))
                .flatMap(entity -> validarTenant(entity, t.getEmpresaId())
                    .then(usuarioQueryRepository.asignarRol(usuarioId, dto.getRolId(), dto.getSedeId()))
                    .thenReturn(true)));
    }

    @Override
    public Mono<Boolean> quitarRol(Long usuarioId, AsignarRolRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            usuarioR2dbcRepository.findById(usuarioId)
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado")))
                .flatMap(entity -> validarTenant(entity, t.getEmpresaId())
                    .then(usuarioQueryRepository.quitarRol(usuarioId, dto.getRolId(), dto.getSedeId()))
                    .thenReturn(true)));
    }

    private Mono<Void> validarTenant(UsuarioEntity entity, Long empresaId) {
        if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
            return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        }
        return Mono.empty();
    }
}
