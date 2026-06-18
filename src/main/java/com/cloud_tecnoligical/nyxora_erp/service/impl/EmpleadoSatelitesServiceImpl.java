package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoEstudioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoFamiliarDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoHistoriaLaboralDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoEstudioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoFamiliarResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoHistoriaLaboralResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoEstudioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoFamiliarDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoHistoriaLaboralDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EmpleadoEstudioEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.EmpleadoFamiliarEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.EmpleadoHistoriaLaboralEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano.EmpleadoEstudioMapper;
import com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano.EmpleadoFamiliarMapper;
import com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano.EmpleadoHistoriaLaboralMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.talentohumano.EmpleadoEstudioR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.talentohumano.EmpleadoFamiliarR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.talentohumano.EmpleadoHistoriaLaboralR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.talentohumano.EmpleadoSatelitesQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.EmpleadoSatelitesService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class EmpleadoSatelitesServiceImpl implements EmpleadoSatelitesService {

    private final EmpleadoEstudioR2dbcRepository estudioRepo;
    private final EmpleadoFamiliarR2dbcRepository familiarRepo;
    private final EmpleadoHistoriaLaboralR2dbcRepository historiaRepo;
    private final EmpleadoEstudioMapper estudioMapper;
    private final EmpleadoFamiliarMapper familiarMapper;
    private final EmpleadoHistoriaLaboralMapper historiaMapper;
    private final EmpleadoSatelitesQueryRepository satQuery;
    private final TerceroQueryRepository terceroQueryRepository;

    public EmpleadoSatelitesServiceImpl(EmpleadoEstudioR2dbcRepository estudioRepo,
            EmpleadoFamiliarR2dbcRepository familiarRepo, EmpleadoHistoriaLaboralR2dbcRepository historiaRepo,
            EmpleadoEstudioMapper estudioMapper, EmpleadoFamiliarMapper familiarMapper,
            EmpleadoHistoriaLaboralMapper historiaMapper, EmpleadoSatelitesQueryRepository satQuery,
            TerceroQueryRepository terceroQueryRepository) {
        this.estudioRepo = estudioRepo;
        this.familiarRepo = familiarRepo;
        this.historiaRepo = historiaRepo;
        this.estudioMapper = estudioMapper;
        this.familiarMapper = familiarMapper;
        this.historiaMapper = historiaMapper;
        this.satQuery = satQuery;
        this.terceroQueryRepository = terceroQueryRepository;
    }

    // ===================== Estudios =====================
    @Override
    public Mono<List<EmpleadoEstudioResponseDto>> listEstudios(Long empleadoId) {
        return validarEmpleado(empleadoId).flatMap(t -> satQuery.listEstudios(empleadoId, t.getEmpresaId()));
    }

    @Override
    public Mono<EmpleadoEstudioResponseDto> createEstudio(Long empleadoId, CreateEmpleadoEstudioDto dto) {
        return validarEmpleado(empleadoId).flatMap(t -> {
            EmpleadoEstudioEntity e = estudioMapper.toEntity(dto);
            e.setEmpresa_id(t.getEmpresaId());
            e.setEmpleado_id(empleadoId);
            e.setConvalidado(Boolean.TRUE.equals(e.getConvalidado()));
            e.setActivo(true);
            e.setCreated_at(LocalDateTime.now());
            return estudioRepo.save(e).map(estudioMapper::toResponseDto);
        });
    }

    @Override
    public Mono<Boolean> updateEstudio(Long empleadoId, UpdateEmpleadoEstudioDto dto) {
        return validarEmpleado(empleadoId).then(estudioRepo.findById(dto.getId())
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getEmpleado_id(), empleadoId)) return noEncontrado();
                estudioMapper.updateEntityFromDto(dto, e);
                if (dto.getConvalidado() != null) e.setConvalidado(dto.getConvalidado());
                if (dto.getActive() != null) e.setActivo(dto.getActive());
                e.setUpdated_at(LocalDateTime.now());
                return estudioRepo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> deleteEstudio(Long empleadoId, Long id) {
        return validarEmpleado(empleadoId).then(estudioRepo.findById(id)
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getEmpleado_id(), empleadoId)) return noEncontrado();
                e.setDeleted_at(LocalDateTime.now());
                return estudioRepo.save(e).thenReturn(true);
            }));
    }

    // ===================== Familiares =====================
    @Override
    public Mono<List<EmpleadoFamiliarResponseDto>> listFamiliares(Long empleadoId) {
        return validarEmpleado(empleadoId).flatMap(t -> satQuery.listFamiliares(empleadoId, t.getEmpresaId()));
    }

    @Override
    public Mono<EmpleadoFamiliarResponseDto> createFamiliar(Long empleadoId, CreateEmpleadoFamiliarDto dto) {
        return validarEmpleado(empleadoId).flatMap(t -> {
            EmpleadoFamiliarEntity e = familiarMapper.toEntity(dto);
            e.setEmpresa_id(t.getEmpresaId());
            e.setEmpleado_id(empleadoId);
            e.setA_cargo(Boolean.TRUE.equals(e.getA_cargo()));
            e.setVivo(e.getVivo() == null ? Boolean.TRUE : e.getVivo());
            e.setConvive(Boolean.TRUE.equals(e.getConvive()));
            e.setDependiente_retencion(Boolean.TRUE.equals(e.getDependiente_retencion()));
            e.setCreated_at(LocalDateTime.now());
            return familiarRepo.save(e).map(familiarMapper::toResponseDto);
        });
    }

    @Override
    public Mono<Boolean> updateFamiliar(Long empleadoId, UpdateEmpleadoFamiliarDto dto) {
        return validarEmpleado(empleadoId).then(familiarRepo.findById(dto.getId())
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getEmpleado_id(), empleadoId)) return noEncontrado();
                familiarMapper.updateEntityFromDto(dto, e);
                if (dto.getACargo() != null) e.setA_cargo(dto.getACargo());
                if (dto.getVivo() != null) e.setVivo(dto.getVivo());
                if (dto.getConvive() != null) e.setConvive(dto.getConvive());
                if (dto.getDependienteRetencion() != null) e.setDependiente_retencion(dto.getDependienteRetencion());
                e.setUpdated_at(LocalDateTime.now());
                return familiarRepo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> deleteFamiliar(Long empleadoId, Long id) {
        return validarEmpleado(empleadoId).then(familiarRepo.findById(id)
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getEmpleado_id(), empleadoId)) return noEncontrado();
                e.setDeleted_at(LocalDateTime.now());
                return familiarRepo.save(e).thenReturn(true);
            }));
    }

    // ===================== Historia laboral =====================
    @Override
    public Mono<List<EmpleadoHistoriaLaboralResponseDto>> listHistorias(Long empleadoId) {
        return validarEmpleado(empleadoId).flatMap(t -> satQuery.listHistorias(empleadoId, t.getEmpresaId()));
    }

    @Override
    public Mono<EmpleadoHistoriaLaboralResponseDto> createHistoria(Long empleadoId, CreateEmpleadoHistoriaLaboralDto dto) {
        return validarEmpleado(empleadoId).flatMap(t -> {
            EmpleadoHistoriaLaboralEntity e = historiaMapper.toEntity(dto);
            e.setEmpresa_id(t.getEmpresaId());
            e.setEmpleado_id(empleadoId);
            e.setEs_publico(Boolean.TRUE.equals(e.getEs_publico()));
            e.setCreated_at(LocalDateTime.now());
            return historiaRepo.save(e).map(historiaMapper::toResponseDto);
        });
    }

    @Override
    public Mono<Boolean> updateHistoria(Long empleadoId, UpdateEmpleadoHistoriaLaboralDto dto) {
        return validarEmpleado(empleadoId).then(historiaRepo.findById(dto.getId())
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getEmpleado_id(), empleadoId)) return noEncontrado();
                historiaMapper.updateEntityFromDto(dto, e);
                if (dto.getEsPublico() != null) e.setEs_publico(dto.getEsPublico());
                e.setUpdated_at(LocalDateTime.now());
                return historiaRepo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> deleteHistoria(Long empleadoId, Long id) {
        return validarEmpleado(empleadoId).then(historiaRepo.findById(id)
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getEmpleado_id(), empleadoId)) return noEncontrado();
                e.setDeleted_at(LocalDateTime.now());
                return historiaRepo.save(e).thenReturn(true);
            }));
    }

    // ===================== helpers =====================
    /** Valida que el empleado sea un tercero activo de la empresa y devuelve el tenant. */
    private Mono<TenantInfo> validarEmpleado(Long empleadoId) {
        return TenantContext.get().flatMap(t ->
            terceroQueryRepository.existsActivoEnEmpresa(empleadoId, t.getEmpresaId())
                .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.just(t)
                    : Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Empleado no encontrado"))));
    }

    private boolean noPertenece(LocalDateTime deletedAt, Long empleadoIdEntity, Long empleadoIdPath) {
        return deletedAt != null || !empleadoIdEntity.equals(empleadoIdPath);
    }

    private <T> Mono<T> noEncontrado() {
        return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Registro no encontrado"));
    }
}
