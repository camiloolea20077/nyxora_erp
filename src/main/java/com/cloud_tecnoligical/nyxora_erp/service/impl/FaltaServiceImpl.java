package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.FaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.FaltaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.UpdateFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FaltaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.juridico.FaltaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.ClasificacionFaltaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.FaltaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.FaltaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.FaltaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class FaltaServiceImpl implements FaltaService {

    private final FaltaR2dbcRepository repo;
    private final FaltaQueryRepository queryRepo;
    private final ClasificacionFaltaQueryRepository clasificacionQueryRepository;
    private final FaltaMapper mapper;

    public FaltaServiceImpl(FaltaR2dbcRepository repo, FaltaQueryRepository queryRepo,
            ClasificacionFaltaQueryRepository clasificacionQueryRepository, FaltaMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.clasificacionQueryRepository = clasificacionQueryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<FaltaResponseDto> create(CreateFaltaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarClasificacion(dto.getClasificacionFaltaId(), t.getEmpresaId())
                .then(queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId())).flatMap(existe -> {
                    if (Boolean.TRUE.equals(existe)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una falta con ese código"));
                    }
                    FaltaEntity e = mapper.toEntity(dto);
                    e.setEmpresa_id(t.getEmpresaId());
                    e.setActivo(true);
                    e.setCreated_at(LocalDateTime.now());
                    return repo.save(e).flatMap(saved -> findById(saved.getId()));
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateFaltaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                validarClasificacion(dto.getClasificacionFaltaId(), t.getEmpresaId())
                    .then(queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId())).flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una falta con ese código"));
                        }
                        e.setClasificacion_falta_id(dto.getClasificacionFaltaId());
                        e.setCodigo(dto.getCodigo());
                        e.setNombre(dto.getNombre());
                        e.setDescripcion(dto.getDescripcion());
                        e.setCaducidad_dias(dto.getCaducidadDias());
                        e.setPolitica(dto.getPolitica());
                        if (dto.getActive() != null) e.setActivo(dto.getActive());
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
    public Mono<FaltaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Falta no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<FaltaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<Void> validarClasificacion(Long clasificacionId, Long empresaId) {
        if (clasificacionId == null) return Mono.empty();
        return clasificacionQueryRepository.existsActivoEnEmpresa(clasificacionId, empresaId).flatMap(ok ->
            Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La clasificación no existe")));
    }

    private Mono<FaltaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Falta no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Falta no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
