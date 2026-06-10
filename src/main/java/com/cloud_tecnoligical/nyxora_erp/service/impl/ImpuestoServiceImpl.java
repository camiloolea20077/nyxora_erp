package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.CreateImpuestoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.ImpuestoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.ImpuestoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.UpdateImpuestoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ImpuestoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.impuesto.ImpuestoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.impuesto.ImpuestoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.impuesto.ImpuestoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ImpuestoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ImpuestoServiceImpl implements ImpuestoService {

    private final ImpuestoR2dbcRepository repository;
    private final ImpuestoQueryRepository queryRepository;
    private final ImpuestoMapper mapper;

    public ImpuestoServiceImpl(ImpuestoR2dbcRepository repository,
                               ImpuestoQueryRepository queryRepository, ImpuestoMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<ImpuestoResponseDto> create(CreateImpuestoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepository.existsByCodigo(dto.getCodigo(), dto.getVigenciaId(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un impuesto con ese código para la vigencia"));
                    }
                    ImpuestoEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    aplicarDefaults(entity);
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateImpuestoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> queryRepository.existsByCodigoExcludingId(
                        dto.getCodigo(), dto.getVigenciaId(), dto.getId(), t.getEmpresaId())
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otro impuesto con ese código para la vigencia"));
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
    public Mono<ImpuestoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Impuesto no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<ImpuestoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<ImpuestoEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Impuesto no encontrado")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Impuesto no encontrado"));
                }
                return Mono.just(entity);
            });
    }

    /** aplica_aiu y retencion_nomina son NOT NULL en BD. */
    private void aplicarDefaults(ImpuestoEntity e) {
        if (e.getAplica_aiu() == null) e.setAplica_aiu(false);
        if (e.getRetencion_nomina() == null) e.setRetencion_nomina(false);
    }
}
