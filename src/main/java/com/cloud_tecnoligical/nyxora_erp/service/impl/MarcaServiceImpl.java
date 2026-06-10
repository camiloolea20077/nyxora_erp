package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateMarcaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MarcaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MarcaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateMarcaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.MarcaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.inventario.MarcaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.MarcaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.MarcaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.MarcaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class MarcaServiceImpl implements MarcaService {

    private final MarcaR2dbcRepository repository;
    private final MarcaQueryRepository queryRepository;
    private final MarcaMapper mapper;

    public MarcaServiceImpl(MarcaR2dbcRepository repository,
                            MarcaQueryRepository queryRepository, MarcaMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<MarcaResponseDto> create(CreateMarcaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepository.existsByCodigo(dto.getCodigo(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una marca con ese código"));
                    }
                    MarcaEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateMarcaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> queryRepository.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId())
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otra marca con ese código"));
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
    public Mono<MarcaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Marca no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<MarcaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<MarcaEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Marca no encontrada")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Marca no encontrada"));
                }
                return Mono.just(entity);
            });
    }
}
