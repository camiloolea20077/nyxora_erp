package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.TerceroEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.tercero.TerceroMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.TerceroService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class TerceroServiceImpl implements TerceroService {

    private final TerceroR2dbcRepository terceroR2dbcRepository;
    private final TerceroQueryRepository terceroQueryRepository;
    private final TerceroMapper terceroMapper;

    public TerceroServiceImpl(TerceroR2dbcRepository terceroR2dbcRepository,
                              TerceroQueryRepository terceroQueryRepository, TerceroMapper terceroMapper) {
        this.terceroR2dbcRepository = terceroR2dbcRepository;
        this.terceroQueryRepository = terceroQueryRepository;
        this.terceroMapper = terceroMapper;
    }

    @Override
    public Mono<TerceroResponseDto> create(CreateTerceroRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            terceroQueryRepository.existsByDocumento(dto.getTipoIdentificacionId(), dto.getNumeroDocumento(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un tercero con ese documento"));
                    }
                    TerceroEntity entity = terceroMapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    entity.setNombre(computeNombre(entity));
                    aplicarDefaultsBooleanos(entity);
                    return terceroR2dbcRepository.save(entity)
                        .flatMap(saved -> terceroQueryRepository.setClasificacion(saved.getId(), dto.getTipoTerceroIds())
                            .then(Mono.fromCallable(() -> {
                                TerceroResponseDto resp = terceroMapper.toResponseDto(saved);
                                resp.setTipoTerceroIds(dto.getTipoTerceroIds());
                                return resp;
                            })));
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateTerceroRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> terceroQueryRepository.existsByDocumentoExcludingId(
                        dto.getTipoIdentificacionId(), dto.getNumeroDocumento(), dto.getId(), t.getEmpresaId())
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otro tercero con ese documento"));
                        }
                        terceroMapper.updateEntityFromDto(dto, entity);
                        entity.setNombre(computeNombre(entity));
                        if (dto.getActive() != null) {
                            entity.setActivo(dto.getActive());
                        }
                        aplicarDefaultsBooleanos(entity);
                        entity.setUsuario_modificacion(t.getUsuarioId());
                        entity.setUpdated_at(LocalDateTime.now());
                        Mono<Void> clasif = dto.getTipoTerceroIds() != null
                            ? terceroQueryRepository.setClasificacion(dto.getId(), dto.getTipoTerceroIds())
                            : Mono.empty();
                        return terceroR2dbcRepository.save(entity).then(clasif).thenReturn(true);
                    })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargar(id, t.getEmpresaId()).flatMap(entity -> {
                entity.setDeleted_at(LocalDateTime.now());
                entity.setUsuario_modificacion(t.getUsuarioId());
                return terceroR2dbcRepository.save(entity).thenReturn(true);
            }));
    }

    @Override
    public Mono<TerceroResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            terceroQueryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Tercero no encontrado")))
                .flatMap(resp -> terceroQueryRepository.findClasificacionIds(resp.getId())
                    .map(ids -> { resp.setTipoTerceroIds(ids); return resp; })));
    }

    @Override
    public Mono<PageResponseDto<TerceroTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> terceroQueryRepository.list(request, t.getEmpresaId()));
    }

    // ---------- helpers ----------

    private Mono<TerceroEntity> cargar(Long id, Long empresaId) {
        return terceroR2dbcRepository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Tercero no encontrado")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Tercero no encontrado"));
                }
                return Mono.just(entity);
            });
    }

    /** nombre normalizado: razón social (jurídica) o nombres+apellidos (natural). */
    private String computeNombre(TerceroEntity e) {
        if ("juridica".equals(e.getTipo_persona())) {
            return e.getRazon_social() != null ? e.getRazon_social().trim() : "";
        }
        return Stream.of(e.getPrimer_nombre(), e.getSegundo_nombre(), e.getPrimer_apellido(), e.getSegundo_apellido())
            .filter(s -> s != null && !s.isBlank())
            .map(String::trim)
            .collect(Collectors.joining(" "));
    }

    /** Los flags fiscales son NOT NULL en BD: si vienen null, false. */
    private void aplicarDefaultsBooleanos(TerceroEntity e) {
        if (e.getResponsable_iva() == null) e.setResponsable_iva(false);
        if (e.getEs_autoretenedor_iva() == null) e.setEs_autoretenedor_iva(false);
        if (e.getEs_autoretenedor_ica() == null) e.setEs_autoretenedor_ica(false);
        if (e.getEs_autoretenedor_fuente() == null) e.setEs_autoretenedor_fuente(false);
        if (e.getDeclarante() == null) e.setDeclarante(false);
        if (e.getAplica_art_383() == null) e.setAplica_art_383(false);
        if (e.getTiene_rut() == null) e.setTiene_rut(false);
        if (e.getEs_reciproco() == null) e.setEs_reciproco(false);
    }
}
