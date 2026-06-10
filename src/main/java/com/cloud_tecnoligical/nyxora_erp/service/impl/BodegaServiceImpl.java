package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateBodegaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateBodegaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.BodegaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.inventario.BodegaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.BodegaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class BodegaServiceImpl implements BodegaService {

    private final BodegaR2dbcRepository repository;
    private final BodegaQueryRepository queryRepository;
    private final BodegaMapper mapper;

    public BodegaServiceImpl(BodegaR2dbcRepository repository,
                             BodegaQueryRepository queryRepository, BodegaMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<BodegaResponseDto> create(CreateBodegaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepository.existsByCodigo(dto.getCodigo(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una bodega con ese código"));
                    }
                    BodegaEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    if (entity.getPermite_compra() == null) entity.setPermite_compra(true);
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateBodegaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> queryRepository.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId())
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otra bodega con ese código"));
                        }
                        mapper.updateEntityFromDto(dto, entity);
                        if (dto.getActive() != null) {
                            entity.setActivo(dto.getActive());
                        }
                        if (entity.getPermite_compra() == null) entity.setPermite_compra(true);
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
    public Mono<BodegaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Bodega no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<BodegaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<BodegaEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Bodega no encontrada")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Bodega no encontrada"));
                }
                return Mono.just(entity);
            });
    }
}
