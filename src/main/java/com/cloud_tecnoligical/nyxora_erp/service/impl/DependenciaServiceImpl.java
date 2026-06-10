package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateDependenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.DependenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.DependenciaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateDependenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.DependenciaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.organizacion.DependenciaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.organizacion.CentroCostoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.organizacion.DependenciaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.organizacion.DependenciaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.DependenciaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class DependenciaServiceImpl implements DependenciaService {

    private final DependenciaR2dbcRepository repository;
    private final DependenciaQueryRepository queryRepository;
    private final CentroCostoQueryRepository centroCostoQueryRepository;
    private final DependenciaMapper mapper;

    public DependenciaServiceImpl(DependenciaR2dbcRepository repository,
                                  DependenciaQueryRepository queryRepository,
                                  CentroCostoQueryRepository centroCostoQueryRepository,
                                  DependenciaMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.centroCostoQueryRepository = centroCostoQueryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<DependenciaResponseDto> create(CreateDependenciaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarCentroCosto(dto.getCentroCostoId(), t.getEmpresaId())
                .then(validarPadre(dto.getDependenciaPadreId(), t.getEmpresaId()))
                .then(queryRepository.existsByCodigo(dto.getCodigo(), t.getEmpresaId()))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una dependencia con ese código"));
                    }
                    DependenciaEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateDependenciaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> validarCentroCosto(dto.getCentroCostoId(), t.getEmpresaId())
                    .then(validarPadreUpdate(dto.getDependenciaPadreId(), dto.getId(), t.getEmpresaId()))
                    .then(queryRepository.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()))
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otra dependencia con ese código"));
                        }
                        mapper.updateEntityFromDto(dto, entity);
                        if (dto.getActive() != null) {
                            entity.setActivo(dto.getActive());
                        }
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
    public Mono<DependenciaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Dependencia no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<DependenciaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<DependenciaEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Dependencia no encontrada")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Dependencia no encontrada"));
                }
                return Mono.just(entity);
            });
    }

    private Mono<Void> validarCentroCosto(Long centroCostoId, Long empresaId) {
        if (centroCostoId == null) {
            return Mono.empty();
        }
        return centroCostoQueryRepository.existsActivoEnEmpresa(centroCostoId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok)
                ? Mono.empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El centro de costo no existe")));
    }

    private Mono<Void> validarPadre(Long padreId, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        return queryRepository.existsActivaEnEmpresa(padreId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok)
                ? Mono.empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La dependencia padre no existe")));
    }

    private Mono<Void> validarPadreUpdate(Long padreId, Long id, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        if (padreId.equals(id)) {
            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Una dependencia no puede ser su propio padre"));
        }
        return validarPadre(padreId, empresaId);
    }
}
