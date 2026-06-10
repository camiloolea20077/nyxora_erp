package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CentroCostoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CentroCostoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateCentroCostoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateCentroCostoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CentroCostoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.organizacion.CentroCostoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.organizacion.CentroCostoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.organizacion.CentroCostoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CentroCostoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class CentroCostoServiceImpl implements CentroCostoService {

    private final CentroCostoR2dbcRepository repository;
    private final CentroCostoQueryRepository queryRepository;
    private final CentroCostoMapper mapper;

    public CentroCostoServiceImpl(CentroCostoR2dbcRepository repository,
                                  CentroCostoQueryRepository queryRepository, CentroCostoMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<CentroCostoResponseDto> create(CreateCentroCostoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarPadre(dto.getCentroCostoPadreId(), t.getEmpresaId())
                .then(queryRepository.existsByCodigo(dto.getCodigo(), t.getEmpresaId()))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un centro de costo con ese código"));
                    }
                    CentroCostoEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    aplicarDefaults(entity);
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateCentroCostoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> validarPadreUpdate(dto.getCentroCostoPadreId(), dto.getId(), t.getEmpresaId())
                    .then(queryRepository.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()))
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otro centro de costo con ese código"));
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
    public Mono<CentroCostoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Centro de costo no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<CentroCostoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<CentroCostoEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Centro de costo no encontrado")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Centro de costo no encontrado"));
                }
                return Mono.just(entity);
            });
    }

    private Mono<Void> validarPadre(Long padreId, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        return queryRepository.existsActivoEnEmpresa(padreId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok)
                ? Mono.empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El centro de costo padre no existe")));
    }

    private Mono<Void> validarPadreUpdate(Long padreId, Long id, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        if (padreId.equals(id)) {
            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Un centro de costo no puede ser su propio padre"));
        }
        return validarPadre(padreId, empresaId);
    }

    private void aplicarDefaults(CentroCostoEntity e) {
        if (e.getEs_observacion() == null) e.setEs_observacion(false);
        if (e.getManeja_plan_financiero() == null) e.setManeja_plan_financiero(false);
    }
}
