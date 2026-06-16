package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.CreateObligacionPagoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.CreateObligacionPagoRetencionDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoTableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ObligacionPagoEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ObligacionPagoRetencionEntity;
import com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar.ObligacionPagoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar.ObligacionPagoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar.ObligacionPagoRetencionR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ObligacionPagoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ObligacionPagoServiceImpl implements ObligacionPagoService {

    private final ObligacionPagoR2dbcRepository repo;
    private final ObligacionPagoRetencionR2dbcRepository retencionRepo;
    private final ObligacionPagoQueryRepository queryRepo;
    private final TransactionalOperator tx;

    public ObligacionPagoServiceImpl(ObligacionPagoR2dbcRepository repo,
                                     ObligacionPagoRetencionR2dbcRepository retencionRepo,
                                     ObligacionPagoQueryRepository queryRepo, ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.retencionRepo = retencionRepo;
        this.queryRepo = queryRepo;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<ObligacionPagoResponseDto> create(CreateObligacionPagoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.terceroExisteEnEmpresa(dto.getProveedorId(), t.getEmpresaId()).flatMap(ok -> {
                if (!Boolean.TRUE.equals(ok)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El proveedor no existe"));
                }
                List<CreateObligacionPagoRetencionDto> retenciones =
                    dto.getRetenciones() != null ? dto.getRetenciones() : List.of();
                BigDecimal totalRet = retenciones.stream().map(r -> nz(r.getValor())).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalRet.compareTo(dto.getValorTotal()) > 0) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Las retenciones superan el valor total"));
                }
                ObligacionPagoEntity o = new ObligacionPagoEntity();
                o.setEmpresa_id(t.getEmpresaId());
                o.setProveedor_id(dto.getProveedorId());
                o.setFactura_proveedor_id(dto.getFacturaProveedorId());
                o.setCuenta_id(dto.getCuentaId());
                o.setNumero(dto.getNumero());
                o.setFecha(dto.getFecha());
                o.setFecha_vencimiento(dto.getFechaVencimiento());
                o.setValor_total(dto.getValorTotal());
                o.setSaldo(dto.getValorTotal().subtract(totalRet));
                o.setEstado("pendiente");
                o.setActivo(true);
                o.setCreated_at(LocalDateTime.now());
                o.setUsuario_creacion(t.getUsuarioId());
                Mono<ObligacionPagoResponseDto> flujo = repo.save(o)
                    .flatMap(saved -> insertarRetenciones(retenciones, saved.getId())
                        .then(cargarRespuesta(saved.getId(), t.getEmpresaId())));
                return flujo.as(tx::transactional);
            }));
    }

    @Override
    public Mono<ObligacionPagoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t -> cargarRespuesta(id, t.getEmpresaId()));
    }

    @Override
    public Mono<PageResponseDto<ObligacionPagoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<Boolean> anular(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(o -> {
                if ("anulada".equals(o.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La obligación ya está anulada"));
                }
                if (nz(o.getSaldo()).compareTo(nz(o.getValor_total())) != 0) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "No se anula una obligación con pagos aplicados"));
                }
                o.setEstado("anulada");
                o.setUsuario_modificacion(t.getUsuarioId());
                o.setUpdated_at(LocalDateTime.now());
                return repo.save(o).thenReturn(true);
            }));
    }

    // ==================== helpers ====================

    private Mono<Void> insertarRetenciones(List<CreateObligacionPagoRetencionDto> retenciones, Long obligacionId) {
        return Flux.fromIterable(retenciones)
            .map(dto -> {
                ObligacionPagoRetencionEntity e = new ObligacionPagoRetencionEntity();
                e.setObligacion_pago_id(obligacionId);
                e.setImpuesto_id(dto.getImpuestoId());
                e.setBase(dto.getBase());
                e.setLimite(dto.getLimite());
                e.setValor(dto.getValor());
                e.setCreated_at(LocalDateTime.now());
                return e;
            })
            .collectList()
            .flatMapMany(retencionRepo::saveAll)
            .then();
    }

    private Mono<ObligacionPagoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Obligación de pago no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Obligación de pago no encontrada"));
                }
                return Mono.just(e);
            });
    }

    private Mono<ObligacionPagoResponseDto> cargarRespuesta(Long id, Long empresaId) {
        return queryRepo.findActiveById(id, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Obligación de pago no encontrada")))
            .flatMap(resp -> queryRepo.listRetenciones(id).map(rs -> { resp.setRetenciones(rs); return resp; }));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
