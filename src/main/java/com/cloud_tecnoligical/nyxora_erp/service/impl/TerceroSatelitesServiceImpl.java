package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroContactoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroCuentaBancariaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroDireccionDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroContactoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroCuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroDireccionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroContactoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroCuentaBancariaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroDireccionDto;
import com.cloud_tecnoligical.nyxora_erp.entity.TerceroContactoEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.TerceroCuentaBancariaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.TerceroDireccionEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.tercero.TerceroContactoMapper;
import com.cloud_tecnoligical.nyxora_erp.mapper.tercero.TerceroCuentaBancariaMapper;
import com.cloud_tecnoligical.nyxora_erp.mapper.tercero.TerceroDireccionMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroContactoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroCuentaBancariaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroDireccionR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroSatelitesQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.TerceroSatelitesService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class TerceroSatelitesServiceImpl implements TerceroSatelitesService {

    private final TerceroContactoR2dbcRepository contactoRepo;
    private final TerceroDireccionR2dbcRepository direccionRepo;
    private final TerceroCuentaBancariaR2dbcRepository cuentaRepo;
    private final TerceroContactoMapper contactoMapper;
    private final TerceroDireccionMapper direccionMapper;
    private final TerceroCuentaBancariaMapper cuentaMapper;
    private final TerceroQueryRepository terceroQueryRepository;
    private final TerceroSatelitesQueryRepository satQuery;

    public TerceroSatelitesServiceImpl(TerceroContactoR2dbcRepository contactoRepo,
            TerceroDireccionR2dbcRepository direccionRepo, TerceroCuentaBancariaR2dbcRepository cuentaRepo,
            TerceroContactoMapper contactoMapper, TerceroDireccionMapper direccionMapper,
            TerceroCuentaBancariaMapper cuentaMapper, TerceroQueryRepository terceroQueryRepository,
            TerceroSatelitesQueryRepository satQuery) {
        this.contactoRepo = contactoRepo;
        this.direccionRepo = direccionRepo;
        this.cuentaRepo = cuentaRepo;
        this.contactoMapper = contactoMapper;
        this.direccionMapper = direccionMapper;
        this.cuentaMapper = cuentaMapper;
        this.terceroQueryRepository = terceroQueryRepository;
        this.satQuery = satQuery;
    }

    // ===================== Contactos =====================
    @Override
    public Mono<List<TerceroContactoResponseDto>> listContactos(Long terceroId) {
        return validarTercero(terceroId).then(satQuery.listContactos(terceroId));
    }

    @Override
    public Mono<TerceroContactoResponseDto> createContacto(Long terceroId, CreateTerceroContactoDto dto) {
        return validarTercero(terceroId).then(Mono.defer(() -> {
            TerceroContactoEntity e = contactoMapper.toEntity(dto);
            e.setTercero_id(terceroId);
            e.setActivo(true);
            e.setPrincipal(Boolean.TRUE.equals(e.getPrincipal()));
            e.setCreated_at(LocalDateTime.now());
            Mono<Long> pre = e.getPrincipal() ? satQuery.unsetPrincipalContacto(terceroId) : Mono.just(0L);
            return pre.then(contactoRepo.save(e)).map(contactoMapper::toResponseDto);
        }));
    }

    @Override
    public Mono<Boolean> updateContacto(Long terceroId, UpdateTerceroContactoDto dto) {
        return validarTercero(terceroId).then(contactoRepo.findById(dto.getId())
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getTercero_id(), terceroId)) return noEncontrado();
                contactoMapper.updateEntityFromDto(dto, e);
                if (dto.getActive() != null) e.setActivo(dto.getActive());
                e.setPrincipal(Boolean.TRUE.equals(e.getPrincipal()));
                e.setUpdated_at(LocalDateTime.now());
                Mono<Long> pre = e.getPrincipal() ? satQuery.unsetPrincipalContacto(terceroId) : Mono.just(0L);
                return pre.then(contactoRepo.save(e)).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> deleteContacto(Long terceroId, Long id) {
        return validarTercero(terceroId).then(contactoRepo.findById(id)
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getTercero_id(), terceroId)) return noEncontrado();
                e.setDeleted_at(LocalDateTime.now());
                return contactoRepo.save(e).thenReturn(true);
            }));
    }

    // ===================== Direcciones =====================
    @Override
    public Mono<List<TerceroDireccionResponseDto>> listDirecciones(Long terceroId) {
        return validarTercero(terceroId).then(satQuery.listDirecciones(terceroId));
    }

    @Override
    public Mono<TerceroDireccionResponseDto> createDireccion(Long terceroId, CreateTerceroDireccionDto dto) {
        return validarTercero(terceroId).then(Mono.defer(() -> {
            TerceroDireccionEntity e = direccionMapper.toEntity(dto);
            e.setTercero_id(terceroId);
            e.setActivo(true);
            if (e.getTipo() == null) e.setTipo("principal");
            e.setPrincipal(Boolean.TRUE.equals(e.getPrincipal()));
            e.setCreated_at(LocalDateTime.now());
            Mono<Long> pre = e.getPrincipal() ? satQuery.unsetPrincipalDireccion(terceroId) : Mono.just(0L);
            return pre.then(direccionRepo.save(e)).map(direccionMapper::toResponseDto);
        }));
    }

    @Override
    public Mono<Boolean> updateDireccion(Long terceroId, UpdateTerceroDireccionDto dto) {
        return validarTercero(terceroId).then(direccionRepo.findById(dto.getId())
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getTercero_id(), terceroId)) return noEncontrado();
                direccionMapper.updateEntityFromDto(dto, e);
                if (dto.getActive() != null) e.setActivo(dto.getActive());
                e.setPrincipal(Boolean.TRUE.equals(e.getPrincipal()));
                e.setUpdated_at(LocalDateTime.now());
                Mono<Long> pre = e.getPrincipal() ? satQuery.unsetPrincipalDireccion(terceroId) : Mono.just(0L);
                return pre.then(direccionRepo.save(e)).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> deleteDireccion(Long terceroId, Long id) {
        return validarTercero(terceroId).then(direccionRepo.findById(id)
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getTercero_id(), terceroId)) return noEncontrado();
                e.setDeleted_at(LocalDateTime.now());
                return direccionRepo.save(e).thenReturn(true);
            }));
    }

    // ===================== Cuentas bancarias =====================
    @Override
    public Mono<List<TerceroCuentaBancariaResponseDto>> listCuentas(Long terceroId) {
        return validarTercero(terceroId).then(satQuery.listCuentas(terceroId));
    }

    @Override
    public Mono<TerceroCuentaBancariaResponseDto> createCuenta(Long terceroId, CreateTerceroCuentaBancariaDto dto) {
        return validarTercero(terceroId).then(Mono.defer(() -> {
            TerceroCuentaBancariaEntity e = cuentaMapper.toEntity(dto);
            e.setTercero_id(terceroId);
            e.setActivo(true);
            e.setPrincipal(Boolean.TRUE.equals(e.getPrincipal()));
            e.setCreated_at(LocalDateTime.now());
            Mono<Long> pre = e.getPrincipal() ? satQuery.unsetPrincipalCuenta(terceroId) : Mono.just(0L);
            return pre.then(cuentaRepo.save(e)).map(cuentaMapper::toResponseDto);
        }));
    }

    @Override
    public Mono<Boolean> updateCuenta(Long terceroId, UpdateTerceroCuentaBancariaDto dto) {
        return validarTercero(terceroId).then(cuentaRepo.findById(dto.getId())
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getTercero_id(), terceroId)) return noEncontrado();
                cuentaMapper.updateEntityFromDto(dto, e);
                if (dto.getActive() != null) e.setActivo(dto.getActive());
                e.setPrincipal(Boolean.TRUE.equals(e.getPrincipal()));
                e.setUpdated_at(LocalDateTime.now());
                Mono<Long> pre = e.getPrincipal() ? satQuery.unsetPrincipalCuenta(terceroId) : Mono.just(0L);
                return pre.then(cuentaRepo.save(e)).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> deleteCuenta(Long terceroId, Long id) {
        return validarTercero(terceroId).then(cuentaRepo.findById(id)
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getTercero_id(), terceroId)) return noEncontrado();
                e.setDeleted_at(LocalDateTime.now());
                return cuentaRepo.save(e).thenReturn(true);
            }));
    }

    // ===================== helpers =====================
    private Mono<Void> validarTercero(Long terceroId) {
        return TenantContext.get().flatMap(t ->
            terceroQueryRepository.existsActivoEnEmpresa(terceroId, t.getEmpresaId())
                .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Tercero no encontrado"))));
    }

    private boolean noPertenece(LocalDateTime deletedAt, Long terceroIdEntity, Long terceroIdPath) {
        return deletedAt != null || !terceroIdEntity.equals(terceroIdPath);
    }

    private <T> Mono<T> noEncontrado() {
        return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Registro no encontrado"));
    }
}
