package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreateDepreciacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.DepreciacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.DepreciacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ActivoFijoEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.DepreciacionEntity;
import com.cloud_tecnoligical.nyxora_erp.repository.activosfijos.ActivoFijoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.activosfijos.DepreciacionQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.activosfijos.DepreciacionR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.DepreciacionService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class DepreciacionServiceImpl implements DepreciacionService {

    private final DepreciacionR2dbcRepository repo;
    private final DepreciacionQueryRepository queryRepo;
    private final ActivoFijoR2dbcRepository activoRepo;
    private final TransactionalOperator tx;

    public DepreciacionServiceImpl(DepreciacionR2dbcRepository repo,
                                   DepreciacionQueryRepository queryRepo,
                                   ActivoFijoR2dbcRepository activoRepo,
                                   ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.activoRepo = activoRepo;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<DepreciacionResponseDto> registrar(CreateDepreciacionRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarActivo(dto.getActivoFijoId(), t.getEmpresaId()).flatMap(activo -> {
                DepreciacionEntity d = new DepreciacionEntity();
                d.setEmpresa_id(t.getEmpresaId());
                d.setActivo_fijo_id(dto.getActivoFijoId());
                d.setFecha_aplicacion(dto.getFechaAplicacion());
                d.setValor_depreciacion(dto.getValorDepreciacion());
                d.setCuota_depreciacion(dto.getCuotaDepreciacion());
                d.setPeriodo_amortizacion(dto.getPeriodoAmortizacion());
                d.setUnidades_producidas(dto.getUnidadesProducidas());
                d.setCreated_at(LocalDateTime.now());
                d.setUsuario_creacion(t.getUsuarioId());

                Mono<DepreciacionEntity> flujo = repo.save(d)
                    .flatMap(saved -> queryRepo.sumByActivo(dto.getActivoFijoId(), t.getEmpresaId())
                        .flatMap(acumulado -> {
                            activo.setValor_depreciacion(acumulado);
                            activo.setMeses_depreciados(nz(activo.getMeses_depreciados()) + 1);
                            activo.setValor_actual(nz(activo.getValor_compra())
                                .subtract(acumulado)
                                .subtract(nz(activo.getDeterioro())));
                            activo.setUsuario_modificacion(t.getUsuarioId());
                            activo.setUpdated_at(LocalDateTime.now());
                            return activoRepo.save(activo).thenReturn(saved);
                        }));

                return flujo.as(tx::transactional)
                    .flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<DepreciacionResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Depreciación no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<DepreciacionTableDto>> listByActivo(Long activoFijoId, PageableDto<?> request) {
        return TenantContext.get().flatMap(t ->
            cargarActivo(activoFijoId, t.getEmpresaId())
                .then(queryRepo.listByActivo(request, activoFijoId, t.getEmpresaId())));
    }

    private Mono<ActivoFijoEntity> cargarActivo(Long activoFijoId, Long empresaId) {
        return activoRepo.findById(activoFijoId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Activo fijo no encontrado")))
            .flatMap(a -> {
                if (a.getDeleted_at() != null || !a.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Activo fijo no encontrado"));
                }
                return Mono.just(a);
            });
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private int nz(Integer v) {
        return v != null ? v : 0;
    }
}
