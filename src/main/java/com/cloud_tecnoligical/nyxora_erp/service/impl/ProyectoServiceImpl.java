package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateProyectoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.ProyectoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.ProyectoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateProyectoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProyectoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.organizacion.ProyectoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.organizacion.ProyectoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.organizacion.ProyectoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ProyectoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoR2dbcRepository repository;
    private final ProyectoQueryRepository queryRepository;
    private final ProyectoMapper mapper;

    public ProyectoServiceImpl(ProyectoR2dbcRepository repository,
                               ProyectoQueryRepository queryRepository, ProyectoMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<ProyectoResponseDto> create(CreateProyectoRequestDto dto) {
        validarFechas(dto.getFechaInicio(), dto.getFechaFinal());
        return TenantContext.get().flatMap(t ->
            queryRepository.existsByCodigo(dto.getCodigo(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un proyecto con ese código"));
                    }
                    ProyectoEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateProyectoRequestDto dto) {
        validarFechas(dto.getFechaInicio(), dto.getFechaFinal());
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> queryRepository.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId())
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otro proyecto con ese código"));
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
    public Mono<ProyectoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<ProyectoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<ProyectoEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Proyecto no encontrado")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
                }
                return Mono.just(entity);
            });
    }

    private void validarFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "La fecha final no puede ser anterior a la fecha de inicio");
        }
    }
}
