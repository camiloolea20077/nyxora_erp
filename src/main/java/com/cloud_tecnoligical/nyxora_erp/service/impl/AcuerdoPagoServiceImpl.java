package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CreateAcuerdoPagoCuotaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CreateAcuerdoPagoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.AcuerdoPagoCuotaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.AcuerdoPagoEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.CuentaPorCobrarEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.cartera.AcuerdoPagoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.cartera.AcuerdoPagoCuotaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.cartera.AcuerdoPagoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.cartera.AcuerdoPagoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.cartera.CuentaPorCobrarR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.AcuerdoPagoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AcuerdoPagoServiceImpl implements AcuerdoPagoService {

    private final AcuerdoPagoR2dbcRepository repo;
    private final AcuerdoPagoCuotaR2dbcRepository cuotaRepo;
    private final AcuerdoPagoQueryRepository queryRepo;
    private final CuentaPorCobrarR2dbcRepository cxcRepo;
    private final AcuerdoPagoMapper mapper;
    private final TransactionalOperator tx;

    public AcuerdoPagoServiceImpl(AcuerdoPagoR2dbcRepository repo, AcuerdoPagoCuotaR2dbcRepository cuotaRepo,
                                  AcuerdoPagoQueryRepository queryRepo, CuentaPorCobrarR2dbcRepository cxcRepo,
                                  AcuerdoPagoMapper mapper, ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.cuotaRepo = cuotaRepo;
        this.queryRepo = queryRepo;
        this.cxcRepo = cxcRepo;
        this.mapper = mapper;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<AcuerdoPagoResponseDto> create(CreateAcuerdoPagoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarCxc(dto.getCuentaPorCobrarId(), t.getEmpresaId()).flatMap(cxc -> {
                if (!"vigente".equals(cxc.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se acuerda una cuenta por cobrar vigente"));
                }
                BigDecimal sumaCuotas = dto.getCuotas().stream()
                    .map(c -> nz(c.getValor())).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (sumaCuotas.compareTo(cxc.getSaldo()) > 0) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La suma de las cuotas supera el saldo de la cuenta por cobrar"));
                }
                AcuerdoPagoEntity a = new AcuerdoPagoEntity();
                a.setEmpresa_id(t.getEmpresaId());
                a.setCuenta_por_cobrar_id(cxc.getId());
                a.setFecha(dto.getFecha());
                a.setNumero_cuotas(dto.getCuotas().size());
                a.setEstado("vigente");
                a.setActivo(true);
                a.setCreated_at(LocalDateTime.now());
                a.setUsuario_creacion(t.getUsuarioId());

                cxc.setEstado("en_acuerdo");
                cxc.setUsuario_modificacion(t.getUsuarioId());
                cxc.setUpdated_at(LocalDateTime.now());

                Mono<AcuerdoPagoResponseDto> flujo = repo.save(a)
                    .flatMap(saved -> insertarCuotas(dto.getCuotas(), saved.getId())
                        .then(cxcRepo.save(cxc))
                        .then(cargarRespuesta(saved.getId(), t.getEmpresaId())));
                return flujo.as(tx::transactional);
            }));
    }

    @Override
    public Mono<AcuerdoPagoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t -> cargarRespuesta(id, t.getEmpresaId()));
    }

    @Override
    public Mono<PageResponseDto<AcuerdoPagoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<Boolean> anular(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(a -> {
                if (!"vigente".equals(a.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se anula un acuerdo vigente"));
                }
                a.setEstado("anulado");
                a.setUsuario_modificacion(t.getUsuarioId());
                a.setUpdated_at(LocalDateTime.now());
                Mono<Void> restaurarCxc = cxcRepo.findById(a.getCuenta_por_cobrar_id()).flatMap(cxc -> {
                    if ("en_acuerdo".equals(cxc.getEstado())) {
                        cxc.setEstado("vigente");
                        cxc.setUsuario_modificacion(t.getUsuarioId());
                        cxc.setUpdated_at(LocalDateTime.now());
                        return cxcRepo.save(cxc).then();
                    }
                    return Mono.empty();
                });
                return repo.save(a).then(restaurarCxc).thenReturn(true).as(tx::transactional);
            }));
    }

    // ==================== helpers ====================

    private Mono<Void> insertarCuotas(List<CreateAcuerdoPagoCuotaDto> cuotas, Long acuerdoId) {
        AtomicInteger n = new AtomicInteger(0);
        return Flux.fromIterable(cuotas)
            .map(dto -> {
                AcuerdoPagoCuotaEntity e = new AcuerdoPagoCuotaEntity();
                e.setAcuerdo_pago_id(acuerdoId);
                e.setNumero_cuota(n.incrementAndGet());
                e.setValor(dto.getValor());
                e.setFecha_aplicacion(dto.getFechaAplicacion());
                e.setEstado("pendiente");
                e.setCreated_at(LocalDateTime.now());
                return e;
            })
            .collectList()
            .flatMapMany(cuotaRepo::saveAll)
            .then();
    }

    private Mono<CuentaPorCobrarEntity> cargarCxc(Long id, Long empresaId) {
        return cxcRepo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La cuenta por cobrar no existe")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La cuenta por cobrar no existe"));
                }
                return Mono.just(e);
            });
    }

    private Mono<AcuerdoPagoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Acuerdo de pago no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Acuerdo de pago no encontrado"));
                }
                return Mono.just(e);
            });
    }

    private Mono<AcuerdoPagoResponseDto> cargarRespuesta(Long id, Long empresaId) {
        return queryRepo.findActiveById(id, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Acuerdo de pago no encontrado")))
            .flatMap(resp -> queryRepo.listCuotas(id).map(cs -> { resp.setCuotas(cs); return resp; }));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
