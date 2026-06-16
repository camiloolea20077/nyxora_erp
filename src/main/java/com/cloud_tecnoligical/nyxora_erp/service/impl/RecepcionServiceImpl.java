package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.ConfirmarRecepcionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateRecepcionLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateRecepcionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateMovimientoContableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.MovimientoInventarioEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.OrdenCompraEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.RecepcionEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.RecepcionLineaEntity;
import com.cloud_tecnoligical.nyxora_erp.event.AsientoContableSolicitado;
import com.cloud_tecnoligical.nyxora_erp.event.DomainEventBus;
import com.cloud_tecnoligical.nyxora_erp.mapper.compras.RecepcionLineaMapper;
import com.cloud_tecnoligical.nyxora_erp.mapper.compras.RecepcionMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.compras.OrdenCompraLineaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.compras.OrdenCompraQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.compras.OrdenCompraR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.compras.RecepcionLineaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.compras.RecepcionQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.compras.RecepcionR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.MovimientoInventarioR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.RecepcionService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RecepcionServiceImpl implements RecepcionService {

    private final RecepcionR2dbcRepository repo;
    private final RecepcionLineaR2dbcRepository lineaRepo;
    private final RecepcionQueryRepository queryRepo;
    private final OrdenCompraR2dbcRepository ordenRepo;
    private final OrdenCompraLineaR2dbcRepository ordenLineaRepo;
    private final OrdenCompraQueryRepository ordenQuery;
    private final MovimientoInventarioR2dbcRepository movInvRepo;
    private final BodegaQueryRepository bodegaQuery;
    private final RecepcionMapper mapper;
    private final RecepcionLineaMapper lineaMapper;
    private final DomainEventBus eventBus;
    private final TransactionalOperator tx;

    public RecepcionServiceImpl(RecepcionR2dbcRepository repo, RecepcionLineaR2dbcRepository lineaRepo,
                                RecepcionQueryRepository queryRepo, OrdenCompraR2dbcRepository ordenRepo,
                                OrdenCompraLineaR2dbcRepository ordenLineaRepo, OrdenCompraQueryRepository ordenQuery,
                                MovimientoInventarioR2dbcRepository movInvRepo, BodegaQueryRepository bodegaQuery,
                                RecepcionMapper mapper, RecepcionLineaMapper lineaMapper,
                                DomainEventBus eventBus, ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.lineaRepo = lineaRepo;
        this.queryRepo = queryRepo;
        this.ordenRepo = ordenRepo;
        this.ordenLineaRepo = ordenLineaRepo;
        this.ordenQuery = ordenQuery;
        this.movInvRepo = movInvRepo;
        this.bodegaQuery = bodegaQuery;
        this.mapper = mapper;
        this.lineaMapper = lineaMapper;
        this.eventBus = eventBus;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<RecepcionResponseDto> create(CreateRecepcionRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarOrden(dto.getOrdenCompraId(), t.getEmpresaId()).flatMap(orden -> {
                if (!"aprobada".equals(orden.getEstado()) && !"recibida_parcial".equals(orden.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La orden debe estar aprobada para recibir"));
                }
                return bodegaQuery.existsActivaEnEmpresa(dto.getBodegaId(), t.getEmpresaId()).flatMap(okBodega -> {
                    if (!Boolean.TRUE.equals(okBodega)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La bodega no existe"));
                    }
                    RecepcionEntity r = new RecepcionEntity();
                    r.setEmpresa_id(t.getEmpresaId());
                    r.setOrden_compra_id(dto.getOrdenCompraId());
                    r.setBodega_id(dto.getBodegaId());
                    r.setTipo_documento_id(dto.getTipoDocumentoId());
                    r.setNumero(dto.getNumero());
                    r.setFecha(dto.getFecha());
                    r.setEstado("borrador");
                    r.setObservaciones(dto.getObservaciones());
                    r.setActivo(true);
                    r.setCreated_at(LocalDateTime.now());
                    r.setUsuario_creacion(t.getUsuarioId());
                    return repo.save(r)
                        .flatMap(saved -> insertarLineas(dto.getLineas(), saved.getId())
                            .then(cargarRespuesta(saved.getId(), t.getEmpresaId())))
                        .as(tx::transactional);
                });
            }));
    }

    @Override
    public Mono<RecepcionResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t -> cargarRespuesta(id, t.getEmpresaId()));
    }

    @Override
    public Mono<PageResponseDto<RecepcionTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<RecepcionResponseDto> confirmar(Long id, ConfirmarRecepcionRequestDto params) {
        return TenantContext.get().flatMap(t ->
            cargarRecepcion(id, t.getEmpresaId()).flatMap(rec -> {
                if (!"borrador".equals(rec.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se confirma una recepción en borrador"));
                }
                return cargarOrden(rec.getOrden_compra_id(), t.getEmpresaId()).flatMap(orden -> {
                    if (!"aprobada".equals(orden.getEstado()) && !"recibida_parcial".equals(orden.getEstado())) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La orden no está en un estado recibible"));
                    }
                    return queryRepo.listLineas(id).flatMap(lineas -> {
                        if (lineas.isEmpty()) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La recepción no tiene líneas"));
                        }
                        Mono<Void> flujo = procesarLineas(lineas, rec, t)
                            .then(actualizarEstadoOrden(orden, t))
                            .then(Mono.defer(() -> {
                                rec.setEstado("confirmada");
                                rec.setUsuario_modificacion(t.getUsuarioId());
                                rec.setUpdated_at(LocalDateTime.now());
                                return repo.save(rec).then();
                            }));
                        return flujo.as(tx::transactional)
                            .then(Mono.fromRunnable(() -> publicarAsiento(params, rec, lineas, t)))
                            .then(cargarRespuesta(id, t.getEmpresaId()));
                    });
                });
            }));
    }

    // ==================== helpers ====================

    private Mono<Void> procesarLineas(List<RecepcionLineaResponseDto> lineas, RecepcionEntity rec, TenantInfo t) {
        return Flux.fromIterable(lineas).concatMap(rl ->
            ordenLineaRepo.findById(rl.getOrdenCompraLineaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La línea de la orden no existe")))
                .flatMap(ol -> {
                    if (!ol.getOrden_compra_id().equals(rec.getOrden_compra_id())) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La línea no pertenece a la orden de la recepción"));
                    }
                    BigDecimal recibidaPrev = nz(ol.getCantidad_recibida());
                    BigDecimal pendiente = ol.getCantidad().subtract(recibidaPrev);
                    if (rl.getCantidadRecibida().compareTo(pendiente) > 0) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La cantidad recibida supera lo pendiente"));
                    }
                    MovimientoInventarioEntity mov = movimientoEntrada(rl, rec, t);
                    BigDecimal nuevaRecibida = recibidaPrev.add(rl.getCantidadRecibida());
                    ol.setCantidad_recibida(nuevaRecibida);
                    ol.setCantidad_pendiente(ol.getCantidad().subtract(nuevaRecibida));
                    ol.setUpdated_at(LocalDateTime.now());
                    return movInvRepo.save(mov).then(ordenLineaRepo.save(ol)).then();
                })
        ).then();
    }

    private MovimientoInventarioEntity movimientoEntrada(RecepcionLineaResponseDto rl, RecepcionEntity rec, TenantInfo t) {
        MovimientoInventarioEntity m = new MovimientoInventarioEntity();
        m.setEmpresa_id(t.getEmpresaId());
        m.setBodega_id(rec.getBodega_id());
        m.setUbicacion_id(nzId(rl.getUbicacionId()));
        m.setProducto_id(rl.getProductoId());
        m.setProducto_variante_id(nzId(rl.getProductoVarianteId()));
        m.setLote_id(nzId(rl.getLoteId()));
        m.setTipo("entrada");
        m.setFecha(rec.getFecha());
        m.setCantidad(rl.getCantidadRecibida());            // entrada → positiva
        m.setCosto_unitario(nz(rl.getCostoUnitario()));
        BigDecimal subtotal = rl.getCantidadRecibida().multiply(nz(rl.getCostoUnitario()));
        m.setSubtotal(subtotal);
        m.setTotal(subtotal);
        m.setDescripcion("Recepción compra #" + rec.getId());
        m.setOrigen_modulo("compras");
        m.setOrigen_id(rec.getId());
        m.setCreated_at(LocalDateTime.now());
        m.setUsuario_creacion(t.getUsuarioId());
        return m;
    }

    private Mono<Void> actualizarEstadoOrden(OrdenCompraEntity orden, TenantInfo t) {
        return ordenQuery.pendienteTotal(orden.getId()).flatMap(pend -> {
            orden.setEstado(pend.signum() == 0 ? "recibida_total" : "recibida_parcial");
            orden.setUsuario_modificacion(t.getUsuarioId());
            orden.setUpdated_at(LocalDateTime.now());
            return ordenRepo.save(orden).then();
        });
    }

    /** Publica el asiento contable si se proveen las 3 cuentas/periodo (consistencia eventual). */
    private void publicarAsiento(ConfirmarRecepcionRequestDto params, RecepcionEntity rec,
                                 List<RecepcionLineaResponseDto> lineas, TenantInfo t) {
        if (params == null || params.getCuentaInventarioId() == null
                || params.getCuentaContrapartidaId() == null || params.getPeriodoContableId() == null) {
            return;
        }
        BigDecimal valor = lineas.stream()
            .map(l -> l.getCantidadRecibida().multiply(nz(l.getCostoUnitario())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (valor.signum() == 0) {
            return;
        }
        CreateMovimientoContableDto debito = new CreateMovimientoContableDto();
        debito.setCuentaId(params.getCuentaInventarioId());
        debito.setDescripcion("Inventario - recepción #" + rec.getId());
        debito.setDebito(valor);
        debito.setCredito(BigDecimal.ZERO);

        CreateMovimientoContableDto credito = new CreateMovimientoContableDto();
        credito.setCuentaId(params.getCuentaContrapartidaId());
        credito.setDescripcion("Contrapartida - recepción #" + rec.getId());
        credito.setDebito(BigDecimal.ZERO);
        credito.setCredito(valor);

        AsientoContableSolicitado evento = new AsientoContableSolicitado(
            t.getEmpresaId(), t.getUsuarioId(), t.getSedeId(),
            params.getPeriodoContableId(), rec.getFecha(),
            "Recepción de compra #" + rec.getId(), "compras", rec.getId(),
            List.of(debito, credito));
        eventBus.publish(evento);
    }

    private Mono<Void> insertarLineas(List<CreateRecepcionLineaDto> lineas, Long recepcionId) {
        return Flux.fromIterable(lineas)
            .map(dto -> {
                RecepcionLineaEntity e = lineaMapper.toEntity(dto);
                e.setRecepcion_id(recepcionId);
                if (e.getCosto_unitario() == null) e.setCosto_unitario(BigDecimal.ZERO);
                e.setCreated_at(LocalDateTime.now());
                return e;
            })
            .collectList()
            .flatMapMany(lineaRepo::saveAll)
            .then();
    }

    private Mono<RecepcionEntity> cargarRecepcion(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Recepción no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Recepción no encontrada"));
                }
                return Mono.just(e);
            });
    }

    private Mono<OrdenCompraEntity> cargarOrden(Long id, Long empresaId) {
        return ordenRepo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La orden de compra no existe")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La orden de compra no existe"));
                }
                return Mono.just(e);
            });
    }

    private Mono<RecepcionResponseDto> cargarRespuesta(Long id, Long empresaId) {
        return queryRepo.findActiveById(id, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Recepción no encontrada")))
            .flatMap(resp -> queryRepo.listLineas(id).map(ls -> { resp.setLineas(ls); return resp; }));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    /** Normaliza un id de FK opcional: 0 (NULL mapeado por MapperRepository) → null para no violar FKs. */
    private static Long nzId(Long v) {
        return (v == null || v == 0L) ? null : v;
    }
}
