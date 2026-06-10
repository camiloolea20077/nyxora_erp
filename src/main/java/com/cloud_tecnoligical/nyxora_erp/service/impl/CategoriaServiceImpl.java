package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.categoria.CategoriaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.categoria.CategoriaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.categoria.CreateCategoriaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.categoria.UpdateCategoriaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CategoriaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.categoria.CategoriaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.categoria.CategoriaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.categoria.CategoriaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CategoriaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaR2dbcRepository repository;
    private final CategoriaQueryRepository queryRepository;
    private final CategoriaMapper mapper;

    public CategoriaServiceImpl(CategoriaR2dbcRepository repository,
                                CategoriaQueryRepository queryRepository, CategoriaMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<CategoriaResponseDto> create(CreateCategoriaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarPadre(dto.getCategoriaPadreId(), t.getEmpresaId())
                .then(queryRepository.existsByCodigo(dto.getCodigo(), t.getEmpresaId()))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una categoría con ese código"));
                    }
                    CategoriaEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateCategoriaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> validarPadreUpdate(dto.getCategoriaPadreId(), dto.getId(), t.getEmpresaId())
                    .then(queryRepository.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()))
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otra categoría con ese código"));
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
    public Mono<CategoriaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Categoría no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<CategoriaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<CategoriaEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Categoría no encontrada")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
                }
                return Mono.just(entity);
            });
    }

    /** El padre (si se envía) debe existir y ser de la empresa. */
    private Mono<Void> validarPadre(Long padreId, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        return queryRepository.existsActivaEnEmpresa(padreId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok)
                ? Mono.empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La categoría padre no existe")));
    }

    private Mono<Void> validarPadreUpdate(Long padreId, Long id, Long empresaId) {
        if (padreId == null) {
            return Mono.empty();
        }
        if (padreId.equals(id)) {
            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Una categoría no puede ser su propio padre"));
        }
        return validarPadre(padreId, empresaId);
    }
}
