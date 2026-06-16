package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateReciboCajaLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateReciboCajaPagoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateReciboCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateMovimientoContableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CajaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.CuentaPorCobrarEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ReciboCajaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ReciboCajaLineaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ReciboCajaPagoEntity;
import com.cloud_tecnoligical.nyxora_erp.event.AsientoContableSolicitado;
import com.cloud_tecnoligical.nyxora_erp.event.DomainEventBus;
import com.cloud_tecnoligical.nyxora_erp.mapper.caja.ReciboCajaPagoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.CajaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.ReciboCajaLineaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.ReciboCajaPagoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.ReciboCajaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.ReciboCajaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.cartera.CuentaPorCobrarR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.ReciboCajaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReciboCajaServiceImpl implements ReciboCajaService {

    private final ReciboCajaR2dbcRepository repo;
    private final ReciboCajaPagoR2dbcRepository pagoRepo;
    private final ReciboCajaLineaR2dbcRepository lineaRepo;
    private final ReciboCajaQueryRepository queryRepo;
    private final CajaR2dbcRepository cajaRepo;
    private final CuentaPorCobrarR2dbcRepository cxcRepo;
    private final ReciboCajaPagoMapper pagoMapper;
    private final DomainEventBus eventBus;
    private final TransactionalOperator tx;

    public ReciboCajaServiceImpl(ReciboCajaR2dbcRepository repo, ReciboCajaPagoR2dbcRepository pagoRepo,
                                 ReciboCajaLineaR2dbcRepository lineaRepo, ReciboCajaQueryRepository queryRepo,
                                 CajaR2dbcRepository cajaRepo, CuentaPorCobrarR2dbcRepository cxcRepo,
                                 ReciboCajaPagoMapper pagoMapper, DomainEventBus eventBus,
                                 ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.pagoRepo = pagoRepo;
        this.lineaRepo = lineaRepo;
        this.queryRepo = queryRepo;
        this.cajaRepo = cajaRepo;
        this.cxcRepo = cxcRepo;
        this.pagoMapper = pagoMapper;
        this.eventBus = eventBus;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<ReciboCajaResponseDto> create(CreateReciboCajaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarCaja(dto.getCajaId(), t.getEmpresaId()).flatMap(caja -> {
                if (!"abierta".equals(caja.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La caja debe estar abierta para recaudar"));
                }
                BigDecimal valor = dto.getPagos().stream().map(p -> nz(p.getValor())).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (valor.signum() <= 0) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El valor del recibo debe ser positivo"));
                }
                List<CreateReciboCajaLineaDto> lineas = dto.getLineas() != null ? dto.getLineas() : List.of();
                BigDecimal aplicado = lineas.stream().map(l -> nz(l.getValorAplicado())).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (aplicado.compareTo(valor) > 0) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La aplicación a cartera supera el valor del recibo"));
                }

                ReciboCajaEntity r = new ReciboCajaEntity();
                r.setEmpresa_id(t.getEmpresaId());
                r.setCaja_id(caja.getId());
                r.setTipo_documento_id(dto.getTipoDocumentoId());
                r.setNumero(dto.getNumero());
                r.setCliente_id(dto.getClienteId());
                r.setFecha(dto.getFecha());
                r.setValor(valor);
                r.setEstado("registrado");
                r.setObservaciones(dto.getObservaciones());
                r.setActivo(true);
                r.setCreated_at(LocalDateTime.now());
                r.setUsuario_creacion(t.getUsuarioId());

                Mono<ReciboCajaResponseDto> flujo = repo.save(r)
                    .flatMap(saved -> insertarPagos(dto.getPagos(), saved.getId())
                        .then(aplicarCartera(lineas, saved.getId(), t))
                        .then(cargarRespuesta(saved.getId(), t.getEmpresaId())));
                return flujo.as(tx::transactional)
                    .doOnSuccess(resp -> publicarAsiento(dto, resp, t));
            }));
    }

    @Override
    public Mono<ReciboCajaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t -> cargarRespuesta(id, t.getEmpresaId()));
    }

    @Override
    public Mono<PageResponseDto<ReciboCajaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<Boolean> anular(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(r -> {
                if (!"registrado".equals(r.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se anula un recibo registrado"));
                }
                r.setEstado("anulado");
                r.setUsuario_modificacion(t.getUsuarioId());
                r.setUpdated_at(LocalDateTime.now());
                Mono<Void> flujo = revertirCartera(id, t).then(repo.save(r).then());
                return flujo.as(tx::transactional).thenReturn(true);
            }));
    }

    // ==================== helpers ====================

    private Mono<Void> insertarPagos(List<CreateReciboCajaPagoDto> pagos, Long reciboId) {
        return Flux.fromIterable(pagos)
            .map(dto -> {
                ReciboCajaPagoEntity e = pagoMapper.toEntity(dto);
                e.setRecibo_caja_id(reciboId);
                e.setCreated_at(LocalDateTime.now());
                return e;
            })
            .collectList()
            .flatMapMany(pagoRepo::saveAll)
            .then();
    }

    /** Aplica el recibo a cada CxC: reduce saldo, marca pagada al llegar a 0, e inserta la línea. */
    private Mono<Void> aplicarCartera(List<CreateReciboCajaLineaDto> lineas, Long reciboId, TenantInfo t) {
        return Flux.fromIterable(lineas).concatMap(l ->
            cargarCxc(l.getCuentaPorCobrarId(), t.getEmpresaId()).flatMap(cxc -> {
                if ("anulada".equals(cxc.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La cuenta por cobrar está anulada"));
                }
                if (l.getValorAplicado().compareTo(nz(cxc.getSaldo())) > 0) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El valor aplicado supera el saldo de la cuenta por cobrar"));
                }
                BigDecimal nuevoSaldo = nz(cxc.getSaldo()).subtract(l.getValorAplicado());
                cxc.setSaldo(nuevoSaldo);
                if (nuevoSaldo.signum() == 0) {
                    cxc.setEstado("pagada");
                }
                cxc.setUsuario_modificacion(t.getUsuarioId());
                cxc.setUpdated_at(LocalDateTime.now());

                ReciboCajaLineaEntity le = new ReciboCajaLineaEntity();
                le.setRecibo_caja_id(reciboId);
                le.setCuenta_por_cobrar_id(cxc.getId());
                le.setValor_aplicado(l.getValorAplicado());
                le.setCreated_at(LocalDateTime.now());
                return cxcRepo.save(cxc).then(lineaRepo.save(le)).then();
            })
        ).then();
    }

    /** Reversa de la aplicación al anular: devuelve el saldo a cada Cxda y la reabre si estaba pagada. */
    private Mono<Void> revertirCartera(Long reciboId, TenantInfo t) {
        return queryRepo.listLineas(reciboId).flatMapMany(Flux::fromIterable).concatMap(l ->
            cxcRepo.findById(l.getCuentaPorCobrarId()).flatMap(cxc -> {
                cxc.setSaldo(nz(cxc.getSaldo()).add(nz(l.getValorAplicado())));
                if ("pagada".equals(cxc.getEstado())) {
                    cxc.setEstado("vigente");
                }
                cxc.setUsuario_modificacion(t.getUsuarioId());
                cxc.setUpdated_at(LocalDateTime.now());
                return cxcRepo.save(cxc).then();
            })
        ).then();
    }

    /** Publica el asiento (débito caja / crédito CxC) si se proveen cuentas + periodo. */
    private void publicarAsiento(CreateReciboCajaRequestDto dto, ReciboCajaResponseDto recibo, TenantInfo t) {
        if (dto.getCuentaCajaId() == null || dto.getCuentaCxcId() == null || dto.getPeriodoContableId() == null) {
            return;
        }
        BigDecimal valor = nz(recibo.getValor());
        if (valor.signum() == 0) {
            return;
        }
        CreateMovimientoContableDto debito = new CreateMovimientoContableDto();
        debito.setCuentaId(dto.getCuentaCajaId());
        debito.setDescripcion("Caja - recibo #" + recibo.getId());
        debito.setDebito(valor);
        debito.setCredito(BigDecimal.ZERO);

        CreateMovimientoContableDto credito = new CreateMovimientoContableDto();
        credito.setCuentaId(dto.getCuentaCxcId());
        credito.setTerceroId(recibo.getClienteId());
        credito.setDescripcion("CxC - recibo #" + recibo.getId());
        credito.setDebito(BigDecimal.ZERO);
        credito.setCredito(valor);

        AsientoContableSolicitado evento = new AsientoContableSolicitado(
            t.getEmpresaId(), t.getUsuarioId(), t.getSedeId(),
            dto.getPeriodoContableId(), recibo.getFecha(),
            "Recibo de caja #" + recibo.getId(), "caja", recibo.getId(),
            List.of(debito, credito));
        eventBus.publish(evento);
    }

    private Mono<CajaEntity> cargarCaja(Long id, Long empresaId) {
        return cajaRepo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La caja no existe")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La caja no existe"));
                }
                return Mono.just(e);
            });
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

    private Mono<ReciboCajaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Recibo de caja no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Recibo de caja no encontrado"));
                }
                return Mono.just(e);
            });
    }

    private Mono<ReciboCajaResponseDto> cargarRespuesta(Long id, Long empresaId) {
        return queryRepo.findActiveById(id, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Recibo de caja no encontrado")))
            .flatMap(resp -> queryRepo.listPagos(id).map(ps -> { resp.setPagos(ps); return resp; }))
            .flatMap(resp -> queryRepo.listLineas(id).map(ls -> { resp.setLineas(ls); return resp; }));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
