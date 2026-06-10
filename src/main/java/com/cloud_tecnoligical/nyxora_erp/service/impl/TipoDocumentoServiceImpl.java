package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.CreateTipoDocumentoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.TipoDocumentoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.TipoDocumentoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.UpdateTipoDocumentoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.TipoDocumentoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.documento.TipoDocumentoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.documento.TipoDocumentoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.documento.TipoDocumentoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.TipoDocumentoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoR2dbcRepository repository;
    private final TipoDocumentoQueryRepository queryRepository;
    private final TipoDocumentoMapper mapper;

    public TipoDocumentoServiceImpl(TipoDocumentoR2dbcRepository repository,
                                    TipoDocumentoQueryRepository queryRepository, TipoDocumentoMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<TipoDocumentoResponseDto> create(CreateTipoDocumentoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepository.existsByCodigo(dto.getCodigo(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un tipo de documento con ese código"));
                    }
                    TipoDocumentoEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    if (entity.getReinicia_por_vigencia() == null) entity.setReinicia_por_vigencia(true);
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateTipoDocumentoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> queryRepository.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId())
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otro tipo de documento con ese código"));
                        }
                        mapper.updateEntityFromDto(dto, entity);
                        if (dto.getActive() != null) {
                            entity.setActivo(dto.getActive());
                        }
                        if (entity.getReinicia_por_vigencia() == null) entity.setReinicia_por_vigencia(true);
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
    public Mono<TipoDocumentoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Tipo de documento no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<TipoDocumentoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<TipoDocumentoEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Tipo de documento no encontrado")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Tipo de documento no encontrado"));
                }
                return Mono.just(entity);
            });
    }
}
