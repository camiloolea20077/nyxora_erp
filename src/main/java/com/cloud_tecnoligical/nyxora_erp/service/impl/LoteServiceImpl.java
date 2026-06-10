package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateLoteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.LoteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.LoteTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateLoteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.LoteEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.inventario.LoteMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.LoteQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.LoteR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.LoteService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class LoteServiceImpl implements LoteService {

    private final LoteR2dbcRepository repository;
    private final LoteQueryRepository queryRepository;
    private final LoteMapper mapper;

    public LoteServiceImpl(LoteR2dbcRepository repository,
                           LoteQueryRepository queryRepository, LoteMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<LoteResponseDto> create(CreateLoteRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepository.existsByCodigo(dto.getCodigo(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un lote con ese código"));
                    }
                    LoteEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateLoteRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> queryRepository.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId())
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otro lote con ese código"));
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
    public Mono<LoteResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Lote no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<LoteTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<LoteEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Lote no encontrado")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Lote no encontrado"));
                }
                return Mono.just(entity);
            });
    }
}
