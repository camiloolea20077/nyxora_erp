package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponsableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateBodegaResponsableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.BodegaResponsableEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.inventario.BodegaResponsableMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaResponsableR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.BodegaResponsableService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class BodegaResponsableServiceImpl implements BodegaResponsableService {

    private final BodegaResponsableR2dbcRepository repo;
    private final BodegaResponsableMapper mapper;
    private final BodegaQueryRepository bodegaQuery;

    public BodegaResponsableServiceImpl(BodegaResponsableR2dbcRepository repo,
                                        BodegaResponsableMapper mapper, BodegaQueryRepository bodegaQuery) {
        this.repo = repo;
        this.mapper = mapper;
        this.bodegaQuery = bodegaQuery;
    }

    @Override
    public Mono<List<BodegaResponsableResponseDto>> list(Long bodegaId) {
        return validarBodega(bodegaId).then(bodegaQuery.listResponsables(bodegaId));
    }

    @Override
    public Mono<BodegaResponsableResponseDto> create(Long bodegaId, CreateBodegaResponsableDto dto) {
        return TenantContext.get().flatMap(t -> validarBodegaEmpresa(bodegaId, t.getEmpresaId())
            .then(bodegaQuery.terceroExisteEnEmpresa(dto.getTerceroId(), t.getEmpresaId()))
            .flatMap(ok -> {
                if (!Boolean.TRUE.equals(ok)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El tercero no existe"));
                }
                BodegaResponsableEntity e = mapper.toEntity(dto);
                e.setBodega_id(bodegaId);
                e.setActivo(true);
                e.setPredeterminado(Boolean.TRUE.equals(e.getPredeterminado()));
                e.setCreated_at(LocalDateTime.now());
                Mono<Long> pre = e.getPredeterminado() ? bodegaQuery.unsetPredeterminado(bodegaId) : Mono.just(0L);
                return pre.then(repo.save(e)).map(mapper::toResponseDto);
            }));
    }

    @Override
    public Mono<Boolean> delete(Long bodegaId, Long id) {
        return validarBodega(bodegaId).then(repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Responsable no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getBodega_id().equals(bodegaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Responsable no encontrado"));
                }
                e.setDeleted_at(LocalDateTime.now());
                return repo.save(e).thenReturn(true);
            }));
    }

    private Mono<Void> validarBodega(Long bodegaId) {
        return TenantContext.get().flatMap(t -> validarBodegaEmpresa(bodegaId, t.getEmpresaId()));
    }

    private Mono<Void> validarBodegaEmpresa(Long bodegaId, Long empresaId) {
        return bodegaQuery.existsActivaEnEmpresa(bodegaId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Bodega no encontrada")));
    }
}
