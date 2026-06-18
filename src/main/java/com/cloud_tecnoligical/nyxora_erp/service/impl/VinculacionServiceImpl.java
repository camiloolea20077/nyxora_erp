package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateVinculacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateVinculacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.VinculacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.VinculacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.VinculacionEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.nomina.VinculacionMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.CargoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.GrupoNominaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.VinculacionQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.VinculacionR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tercero.TerceroQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.VinculacionService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class VinculacionServiceImpl implements VinculacionService {

    private final VinculacionR2dbcRepository repo;
    private final VinculacionQueryRepository queryRepo;
    private final VinculacionMapper mapper;
    private final TerceroQueryRepository terceroQueryRepository;
    private final CargoQueryRepository cargoQueryRepository;
    private final GrupoNominaQueryRepository grupoQueryRepository;

    public VinculacionServiceImpl(VinculacionR2dbcRepository repo, VinculacionQueryRepository queryRepo,
            VinculacionMapper mapper, TerceroQueryRepository terceroQueryRepository,
            CargoQueryRepository cargoQueryRepository, GrupoNominaQueryRepository grupoQueryRepository) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
        this.terceroQueryRepository = terceroQueryRepository;
        this.cargoQueryRepository = cargoQueryRepository;
        this.grupoQueryRepository = grupoQueryRepository;
    }

    @Override
    public Mono<VinculacionResponseDto> create(CreateVinculacionRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                VinculacionEntity e = new VinculacionEntity();
                mapper.apply(dto, e);
                e.setEmpresa_id(t.getEmpresaId());
                e.setSueldo(dto.getSueldo());
                e.setFecha(dto.getFecha());
                e.setObjeto(dto.getObjeto());
                e.setPeriodo_prueba(Boolean.TRUE.equals(dto.getPeriodoPrueba()));
                e.setTemporal(Boolean.TRUE.equals(dto.getTemporal()));
                if (e.getEstado_vinculacion() == null) e.setEstado_vinculacion("activa");
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateVinculacionRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                    mapper.apply(dto, e);
                    e.setSueldo(dto.getSueldo());
                    e.setFecha(dto.getFecha());
                    e.setObjeto(dto.getObjeto());
                    e.setPeriodo_prueba(Boolean.TRUE.equals(dto.getPeriodoPrueba()));
                    e.setTemporal(Boolean.TRUE.equals(dto.getTemporal()));
                    if (dto.getActive() != null) e.setActivo(dto.getActive());
                    e.setUpdated_at(LocalDateTime.now());
                    e.setUsuario_modificacion(t.getUsuarioId());
                    return repo.save(e).thenReturn(true);
                }))));
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
    public Mono<VinculacionResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Vinculación no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<VinculacionTableDto>> list(PageableDto<?> request, Long empleadoId) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId(), empleadoId));
    }

    private Mono<Void> validarReferencias(CreateVinculacionRequestDto dto, Long empresaId) {
        Mono<Void> chain = terceroQueryRepository.existsActivoEnEmpresa(dto.getEmpleadoId(), empresaId).flatMap(ok ->
            Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Empleado no encontrado")));
        if (dto.getCargoId() != null) {
            chain = chain.then(cargoQueryRepository.existsActivoEnEmpresa(dto.getCargoId(), empresaId).flatMap(ok ->
                Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El cargo no existe"))));
        }
        if (dto.getGrupoNominaId() != null) {
            chain = chain.then(grupoQueryRepository.existsActivoEnEmpresa(dto.getGrupoNominaId(), empresaId).flatMap(ok ->
                Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El grupo de nómina no existe"))));
        }
        if (dto.getJefeId() != null) {
            chain = chain.then(terceroQueryRepository.existsActivoEnEmpresa(dto.getJefeId(), empresaId).flatMap(ok ->
                Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El jefe no existe"))));
        }
        return chain;
    }

    private Mono<VinculacionEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Vinculación no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Vinculación no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
