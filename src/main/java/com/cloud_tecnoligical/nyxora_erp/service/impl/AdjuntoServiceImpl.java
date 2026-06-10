package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.adjunto.AdjuntoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.adjunto.CreateAdjuntoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.AdjuntoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.adjunto.AdjuntoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.adjunto.AdjuntoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.adjunto.AdjuntoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.AdjuntoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class AdjuntoServiceImpl implements AdjuntoService {

    private final AdjuntoR2dbcRepository repository;
    private final AdjuntoQueryRepository queryRepository;
    private final AdjuntoMapper mapper;

    public AdjuntoServiceImpl(AdjuntoR2dbcRepository repository,
                              AdjuntoQueryRepository queryRepository, AdjuntoMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<AdjuntoResponseDto> create(CreateAdjuntoRequestDto dto) {
        return TenantContext.get().flatMap(t -> {
            AdjuntoEntity entity = mapper.toEntity(dto);
            entity.setEmpresa_id(t.getEmpresaId());
            entity.setUsuario_creacion(t.getUsuarioId());
            entity.setActivo(true);
            entity.setCreated_at(LocalDateTime.now());
            return repository.save(entity).map(mapper::toResponseDto);
        });
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            repository.findById(id)
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Adjunto no encontrado")))
                .flatMap(entity -> {
                    if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(t.getEmpresaId())) {
                        return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Adjunto no encontrado"));
                    }
                    entity.setDeleted_at(LocalDateTime.now());
                    return repository.save(entity).thenReturn(true);
                }));
    }

    @Override
    public Mono<AdjuntoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Adjunto no encontrado"))));
    }

    @Override
    public Mono<List<AdjuntoResponseDto>> listByObjeto(String modulo, String entidad, Long entidadId) {
        return TenantContext.get().flatMap(t ->
            queryRepository.listByObjeto(modulo, entidad, entidadId, t.getEmpresaId()));
    }
}
