package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CargoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CargoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateCargoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateCargoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CargoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.nomina.CargoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.CargoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.CargoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CargoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class CargoServiceImpl implements CargoService {

    private final CargoR2dbcRepository repo;
    private final CargoQueryRepository queryRepo;
    private final CargoMapper mapper;

    public CargoServiceImpl(CargoR2dbcRepository repo, CargoQueryRepository queryRepo, CargoMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<CargoResponseDto> create(CreateCargoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un cargo con ese código"));
                }
                CargoEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateCargoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un cargo con ese código"));
                    }
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    e.setNivel_cargo(dto.getNivelCargo());
                    e.setGrado(dto.getGrado());
                    e.setTipo_remuneracion(dto.getTipoRemuneracion());
                    e.setSueldo_basico(dto.getSueldoBasico());
                    e.setSueldo_maximo(dto.getSueldoMaximo());
                    e.setMision(dto.getMision());
                    e.setDescripcion(dto.getDescripcion());
                    if (dto.getActive() != null) e.setActivo(dto.getActive());
                    e.setUpdated_at(LocalDateTime.now());
                    e.setUsuario_modificacion(t.getUsuarioId());
                    return repo.save(e).thenReturn(true);
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
    public Mono<CargoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cargo no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<CargoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<CargoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cargo no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cargo no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
