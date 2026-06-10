package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateUbicacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UbicacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UbicacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateUbicacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.UbicacionEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.inventario.UbicacionMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.UbicacionQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.UbicacionR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.UbicacionService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionR2dbcRepository repository;
    private final UbicacionQueryRepository queryRepository;
    private final BodegaQueryRepository bodegaQueryRepository;
    private final UbicacionMapper mapper;

    public UbicacionServiceImpl(UbicacionR2dbcRepository repository,
                                UbicacionQueryRepository queryRepository,
                                BodegaQueryRepository bodegaQueryRepository, UbicacionMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.bodegaQueryRepository = bodegaQueryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<UbicacionResponseDto> create(CreateUbicacionRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarBodega(dto.getBodegaId(), t.getEmpresaId())
                .then(validarPadre(dto.getUbicacionPadreId(), t.getEmpresaId()))
                .then(queryRepository.existsByCodigoEnBodega(dto.getCodigo(), dto.getBodegaId()))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una ubicación con ese código en la bodega"));
                    }
                    UbicacionEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateUbicacionRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> validarBodega(dto.getBodegaId(), t.getEmpresaId())
                    .then(validarPadreUpdate(dto.getUbicacionPadreId(), dto.getId(), t.getEmpresaId()))
                    .then(queryRepository.existsByCodigoEnBodegaExcludingId(dto.getCodigo(), dto.getBodegaId(), dto.getId()))
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otra ubicación con ese código en la bodega"));
                        }
                        mapper.updateEntityFromDto(dto, entity);
                        if (dto.getActive() != null) {
                            entity.setActivo(dto.getActive());
                        }
                        entity.setUpdated_at(LocalDateTime.now());
                        return repository.save(entity).thenReturn(true);
                    })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargar(id, t.getEmpresaId()).flatMap(entity -> {
                entity.setDeleted_at(LocalDateTime.now());
                return repository.save(entity).thenReturn(true);
            }));
    }

    @Override
    public Mono<UbicacionResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Ubicación no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<UbicacionTableDto>> list(PageableDto<?> request, Long bodegaId) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId(), bodegaId));
    }

    private Mono<UbicacionEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Ubicación no encontrada")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Ubicación no encontrada"));
                }
                return Mono.just(entity);
            });
    }

    private Mono<Void> validarBodega(Long bodegaId, Long empresaId) {
        return bodegaQueryRepository.existsActivaEnEmpresa(bodegaId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La bodega no existe")));
    }

    private Mono<Void> validarPadre(Long padreId, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        return queryRepository.existsActivaEnEmpresa(padreId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La ubicación padre no existe")));
    }

    private Mono<Void> validarPadreUpdate(Long padreId, Long id, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        if (padreId.equals(id)) {
            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Una ubicación no puede ser su propio padre"));
        }
        return validarPadre(padreId, empresaId);
    }
}
