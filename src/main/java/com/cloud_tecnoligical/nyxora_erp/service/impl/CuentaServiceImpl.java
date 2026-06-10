package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateCuentaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CuentaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CuentaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.UpdateCuentaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CuentaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.contabilidad.CuentaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.CuentaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.CuentaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CuentaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class CuentaServiceImpl implements CuentaService {

    private final CuentaR2dbcRepository repository;
    private final CuentaQueryRepository queryRepository;
    private final CuentaMapper mapper;

    public CuentaServiceImpl(CuentaR2dbcRepository repository,
                             CuentaQueryRepository queryRepository, CuentaMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<CuentaResponseDto> create(CreateCuentaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarPadre(dto.getCuentaPadreId(), t.getEmpresaId())
                .then(queryRepository.existsByCodigo(dto.getCodigoCuenta(), t.getEmpresaId()))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una cuenta con ese código"));
                    }
                    CuentaEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    aplicarDefaults(entity);
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateCuentaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> validarPadreUpdate(dto.getCuentaPadreId(), dto.getId(), t.getEmpresaId())
                    .then(queryRepository.existsByCodigoExcludingId(dto.getCodigoCuenta(), dto.getId(), t.getEmpresaId()))
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otra cuenta con ese código"));
                        }
                        mapper.updateEntityFromDto(dto, entity);
                        if (dto.getActive() != null) {
                            entity.setActivo(dto.getActive());
                        }
                        aplicarDefaults(entity);
                        entity.setUsuario_modificacion(t.getUsuarioId());
                        entity.setUpdated_at(LocalDateTime.now());
                        return repository.save(entity).thenReturn(true);
                    })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargar(id, t.getEmpresaId()).flatMap(entity -> {
                entity.setDeleted_at(LocalDateTime.now());
                entity.setUsuario_modificacion(t.getUsuarioId());
                return repository.save(entity).thenReturn(true);
            }));
    }

    @Override
    public Mono<CuentaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<CuentaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<CuentaEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cuenta no encontrada")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
                }
                return Mono.just(entity);
            });
    }

    private Mono<Void> validarPadre(Long padreId, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        return queryRepository.existsActivaEnEmpresa(padreId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok)
                ? Mono.empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La cuenta padre no existe")));
    }

    private Mono<Void> validarPadreUpdate(Long padreId, Long id, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        if (padreId.equals(id)) {
            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Una cuenta no puede ser su propio padre"));
        }
        return validarPadre(padreId, empresaId);
    }

    /** Los flags maneja_* son NOT NULL en BD: si vienen null, false. */
    private void aplicarDefaults(CuentaEntity e) {
        if (e.getManeja_movimiento() == null) e.setManeja_movimiento(false);
        if (e.getManeja_movimiento_manual() == null) e.setManeja_movimiento_manual(false);
        if (e.getManeja_tercero() == null) e.setManeja_tercero(false);
        if (e.getManeja_centro_costo() == null) e.setManeja_centro_costo(false);
        if (e.getManeja_impuesto() == null) e.setManeja_impuesto(false);
        if (e.getManeja_proyecto() == null) e.setManeja_proyecto(false);
        if (e.getManeja_recurso() == null) e.setManeja_recurso(false);
        if (e.getManeja_saldo_contrario() == null) e.setManeja_saldo_contrario(false);
    }
}
