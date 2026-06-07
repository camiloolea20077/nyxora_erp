package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.CreateEmpresaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.EmpresaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.EmpresaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.UpdateEmpresaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EmpresaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.empresa.EmpresaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.empresa.EmpresaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.empresa.EmpresaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.EmpresaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaR2dbcRepository empresaR2dbcRepository;
    private final EmpresaQueryRepository empresaQueryRepository;
    private final EmpresaMapper empresaMapper;

    public EmpresaServiceImpl(EmpresaR2dbcRepository empresaR2dbcRepository,
                              EmpresaQueryRepository empresaQueryRepository, EmpresaMapper empresaMapper) {
        this.empresaR2dbcRepository = empresaR2dbcRepository;
        this.empresaQueryRepository = empresaQueryRepository;
        this.empresaMapper = empresaMapper;
    }

    @Override
    public Mono<EmpresaResponseDto> create(CreateEmpresaRequestDto dto) {
        return TenantContext.get().flatMap(t -> {
            if (!t.isSuperAdmin()) {
                return Mono.error(new GlobalException(HttpStatus.FORBIDDEN, "Solo un super-administrador puede crear empresas"));
            }
            return empresaQueryRepository.existsByNit(dto.getNit())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una empresa con ese NIT"));
                    }
                    EmpresaEntity entity = empresaMapper.toEntity(dto);
                    entity.setActivo(true);
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setCreated_at(LocalDateTime.now());
                    return empresaR2dbcRepository.save(entity).map(empresaMapper::toResponseDto);
                });
        });
    }

    @Override
    public Mono<Boolean> update(UpdateEmpresaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            empresaR2dbcRepository.findById(dto.getId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Empresa no encontrada")))
                .flatMap(entity -> validarAcceso(entity, t).then(Mono.defer(() -> {
                    empresaMapper.updateEntityFromDto(dto, entity);
                    if (dto.getActive() != null) {
                        entity.setActivo(dto.getActive());
                    }
                    entity.setUsuario_modificacion(t.getUsuarioId());
                    entity.setUpdated_at(LocalDateTime.now());
                    return empresaR2dbcRepository.save(entity).thenReturn(true);
                }))));
    }

    @Override
    public Mono<EmpresaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t -> {
            if (!t.isSuperAdmin() && !id.equals(t.getEmpresaId())) {
                return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
            }
            return empresaQueryRepository.findById(id)
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Empresa no encontrada")));
        });
    }

    @Override
    public Mono<EmpresaResponseDto> findActual() {
        return TenantContext.get().flatMap(t ->
            empresaQueryRepository.findById(t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Empresa no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<EmpresaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> {
            if (!t.isSuperAdmin()) {
                return Mono.error(new GlobalException(HttpStatus.FORBIDDEN, "Solo un super-administrador puede listar todas las empresas"));
            }
            return empresaQueryRepository.list(request);
        });
    }

    // super-admin: cualquiera; usuario normal: solo su propia empresa
    private Mono<Void> validarAcceso(EmpresaEntity entity, TenantInfo t) {
        if (entity.getDeleted_at() != null) {
            return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
        }
        if (!t.isSuperAdmin() && !entity.getId().equals(t.getEmpresaId())) {
            return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
        }
        return Mono.empty();
    }
}
