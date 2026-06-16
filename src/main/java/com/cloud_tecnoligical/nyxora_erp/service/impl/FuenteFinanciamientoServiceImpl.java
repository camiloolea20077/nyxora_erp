package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateFuenteFinanciamientoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.FuenteFinanciamientoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.FuenteFinanciamientoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.UpdateFuenteFinanciamientoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FuenteFinanciamientoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.presupuesto.FuenteFinanciamientoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.FuenteFinanciamientoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.FuenteFinanciamientoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.FuenteFinanciamientoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class FuenteFinanciamientoServiceImpl implements FuenteFinanciamientoService {

    private final FuenteFinanciamientoR2dbcRepository repo;
    private final FuenteFinanciamientoQueryRepository queryRepo;
    private final FuenteFinanciamientoMapper mapper;

    public FuenteFinanciamientoServiceImpl(FuenteFinanciamientoR2dbcRepository repo,
                                           FuenteFinanciamientoQueryRepository queryRepo,
                                           FuenteFinanciamientoMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<FuenteFinanciamientoResponseDto> create(CreateFuenteFinanciamientoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una fuente con ese código"));
                }
                FuenteFinanciamientoEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateFuenteFinanciamientoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una fuente con ese código"));
                    }
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    e.setDescripcion(dto.getDescripcion());
                    e.setTipo_recurso(dto.getTipoRecurso());
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
    public Mono<FuenteFinanciamientoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Fuente de financiamiento no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<FuenteFinanciamientoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<FuenteFinanciamientoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Fuente de financiamiento no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Fuente de financiamiento no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
