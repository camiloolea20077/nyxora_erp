package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateRubroPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.RubroPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.RubroPresupuestalTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.UpdateRubroPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.RubroPresupuestalEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.presupuesto.RubroPresupuestalMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.RubroPresupuestalQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.RubroPresupuestalR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.RubroPresupuestalService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class RubroPresupuestalServiceImpl implements RubroPresupuestalService {

    private final RubroPresupuestalR2dbcRepository repo;
    private final RubroPresupuestalQueryRepository queryRepo;
    private final RubroPresupuestalMapper mapper;

    public RubroPresupuestalServiceImpl(RubroPresupuestalR2dbcRepository repo,
                                        RubroPresupuestalQueryRepository queryRepo,
                                        RubroPresupuestalMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<RubroPresupuestalResponseDto> create(CreateRubroPresupuestalRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigoRubro(), dto.getVigenciaId(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un rubro con ese código en la vigencia"));
                }
                return nivelHijo(dto.getRubroPadreId(), t.getEmpresaId()).flatMap(nivel -> {
                    RubroPresupuestalEntity e = mapper.toEntity(dto);
                    e.setEmpresa_id(t.getEmpresaId());
                    e.setNivel(nivel);
                    if (e.getManeja_movimiento() == null) e.setManeja_movimiento(false);
                    e.setActivo(true);
                    e.setCreated_at(LocalDateTime.now());
                    e.setUsuario_creacion(t.getUsuarioId());
                    return repo.save(e).flatMap(saved -> findById(saved.getId()));
                });
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateRubroPresupuestalRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigoRubro(), dto.getVigenciaId(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un rubro con ese código en la vigencia"));
                    }
                    return nivelHijo(dto.getRubroPadreId(), t.getEmpresaId()).flatMap(nivel -> {
                        e.setVigencia_id(dto.getVigenciaId());
                        e.setRubro_padre_id(dto.getRubroPadreId());
                        e.setTipo_rubro(dto.getTipoRubro());
                        e.setCodigo_rubro(dto.getCodigoRubro());
                        e.setNombre_rubro(dto.getNombreRubro());
                        if (dto.getManejaMovimiento() != null) e.setManeja_movimiento(dto.getManejaMovimiento());
                        e.setHomologacion_circular_unica(dto.getHomologacionCircularUnica());
                        e.setNivel(nivel);
                        e.setUsuario_modificacion(t.getUsuarioId());
                        e.setUpdated_at(LocalDateTime.now());
                        return repo.save(e).thenReturn(true);
                    });
                })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setDeleted_at(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<RubroPresupuestalResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Rubro presupuestal no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<RubroPresupuestalTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    /** Nivel del hijo = nivel del padre + 1; 1 si es raíz. */
    private Mono<Integer> nivelHijo(Long padreId, Long empresaId) {
        if (padreId == null) {
            return Mono.just(1);
        }
        return queryRepo.nivelDePadre(padreId, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El rubro padre no existe")))
            .map(nivelPadre -> nivelPadre + 1);
    }

    private Mono<RubroPresupuestalEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Rubro presupuestal no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Rubro presupuestal no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
