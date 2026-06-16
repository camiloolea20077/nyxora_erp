package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateModalidadContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ModalidadContratoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ModalidadContratoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.UpdateModalidadContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ModalidadContratoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.contratacion.ModalidadContratoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.contratacion.ModalidadContratoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contratacion.ModalidadContratoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ModalidadContratoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ModalidadContratoServiceImpl implements ModalidadContratoService {

    private final ModalidadContratoR2dbcRepository repo;
    private final ModalidadContratoQueryRepository queryRepo;
    private final ModalidadContratoMapper mapper;

    public ModalidadContratoServiceImpl(ModalidadContratoR2dbcRepository repo,
                                        ModalidadContratoQueryRepository queryRepo,
                                        ModalidadContratoMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<ModalidadContratoResponseDto> create(CreateModalidadContratoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una modalidad con ese código"));
                }
                ModalidadContratoEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateModalidadContratoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una modalidad con ese código"));
                    }
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    e.setDescripcion(dto.getDescripcion());
                    e.setUpdated_at(LocalDateTime.now());
                    return repo.save(e).thenReturn(true);
                })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setDeleted_at(LocalDateTime.now());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<ModalidadContratoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Modalidad no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<ModalidadContratoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<ModalidadContratoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Modalidad no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Modalidad no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
