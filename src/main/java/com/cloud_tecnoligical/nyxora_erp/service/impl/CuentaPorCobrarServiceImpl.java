package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CreateCuentaPorCobrarRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CuentaPorCobrarResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CuentaPorCobrarTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CuentaPorCobrarEntity;
import com.cloud_tecnoligical.nyxora_erp.event.CuentaPorCobrarSolicitada;
import com.cloud_tecnoligical.nyxora_erp.repository.cartera.CuentaPorCobrarQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.cartera.CuentaPorCobrarR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CuentaPorCobrarService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class CuentaPorCobrarServiceImpl implements CuentaPorCobrarService {

    private final CuentaPorCobrarR2dbcRepository repo;
    private final CuentaPorCobrarQueryRepository queryRepo;

    public CuentaPorCobrarServiceImpl(CuentaPorCobrarR2dbcRepository repo, CuentaPorCobrarQueryRepository queryRepo) {
        this.repo = repo;
        this.queryRepo = queryRepo;
    }

    @Override
    public Mono<CuentaPorCobrarResponseDto> create(CreateCuentaPorCobrarRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.terceroExisteEnEmpresa(dto.getClienteId(), t.getEmpresaId()).flatMap(ok -> {
                if (!Boolean.TRUE.equals(ok)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El cliente no existe"));
                }
                CuentaPorCobrarEntity e = new CuentaPorCobrarEntity();
                e.setEmpresa_id(t.getEmpresaId());
                e.setCliente_id(dto.getClienteId());
                e.setCuenta_id(dto.getCuentaId());
                e.setFecha_emision(dto.getFechaEmision());
                e.setFecha_vencimiento(dto.getFechaVencimiento());
                e.setDias(dto.getDias() != null ? dto.getDias() : calcularDias(dto.getFechaEmision(), dto.getFechaVencimiento()));
                e.setValor_total(dto.getValorTotal());
                e.setValor_interes(BigDecimal.ZERO);
                e.setSaldo(dto.getValorTotal());
                e.setEstado("vigente");
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<CuentaPorCobrarResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cuenta por cobrar no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<CuentaPorCobrarTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<CuentaPorCobrarEntity> crearDesdeEvento(CuentaPorCobrarSolicitada ev) {
        CuentaPorCobrarEntity e = new CuentaPorCobrarEntity();
        e.setEmpresa_id(ev.getEmpresaId());
        e.setCliente_id(ev.getClienteId());
        e.setFactura_id(ev.getFacturaId());
        e.setFecha_emision(ev.getFecha());
        e.setFecha_vencimiento(ev.getFechaVencimiento());
        e.setDias(calcularDias(ev.getFecha(), ev.getFechaVencimiento()));
        e.setValor_total(ev.getValor());
        e.setValor_interes(BigDecimal.ZERO);
        e.setSaldo(ev.getValor());
        e.setEstado("vigente");
        e.setActivo(true);
        e.setCreated_at(LocalDateTime.now());
        e.setUsuario_creacion(ev.getUsuarioId());
        return repo.save(e);
    }

    private Integer calcularDias(java.time.LocalDate desde, java.time.LocalDate hasta) {
        if (desde == null || hasta == null) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(desde, hasta);
    }
}
