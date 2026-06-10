package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreatePeriodoContableRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.PeriodoContableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.PeriodoContableTableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.PeriodoContableEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.contabilidad.PeriodoContableMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.PeriodoContableQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.PeriodoContableR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.PeriodoContableService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class PeriodoContableServiceImpl implements PeriodoContableService {

    private final PeriodoContableR2dbcRepository repository;
    private final PeriodoContableQueryRepository queryRepository;
    private final PeriodoContableMapper mapper;

    public PeriodoContableServiceImpl(PeriodoContableR2dbcRepository repository,
                                      PeriodoContableQueryRepository queryRepository,
                                      PeriodoContableMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<PeriodoContableResponseDto> create(CreatePeriodoContableRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepository.existsByAnioMes(dto.getAnio(), dto.getMes(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe el periodo para ese año y mes"));
                    }
                    PeriodoContableEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setEstado("abierto");
                    entity.setCreated_at(LocalDateTime.now());
                    return repository.save(entity).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<PeriodoContableResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Periodo contable no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<PeriodoContableTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<Boolean> cerrar(Long id) {
        return cambiarEstado(id, "cerrado");
    }

    @Override
    public Mono<Boolean> reabrir(Long id) {
        return cambiarEstado(id, "abierto");
    }

    private Mono<Boolean> cambiarEstado(Long id, String nuevoEstado) {
        return TenantContext.get().flatMap(t ->
            cargar(id, t.getEmpresaId()).flatMap(entity -> {
                if (nuevoEstado.equals(entity.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El periodo ya está " + nuevoEstado));
                }
                entity.setEstado(nuevoEstado);
                entity.setFecha_cierre("cerrado".equals(nuevoEstado) ? LocalDateTime.now() : null);
                entity.setUsuario_modificacion(t.getUsuarioId());
                entity.setUpdated_at(LocalDateTime.now());
                return repository.save(entity).thenReturn(true);
            }));
    }

    private Mono<PeriodoContableEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Periodo contable no encontrado")))
            .flatMap(entity -> {
                if (!entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Periodo contable no encontrado"));
                }
                return Mono.just(entity);
            });
    }
}
