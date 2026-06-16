package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateMovimientoContableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.CreateFacturaLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.CreateFacturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.EmitirFacturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.RegistrarFacturaDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.UpdateFacturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FacturaDianEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.FacturaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.FacturaLineaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.MovimientoInventarioEntity;
import com.cloud_tecnoligical.nyxora_erp.event.AsientoContableSolicitado;
import com.cloud_tecnoligical.nyxora_erp.event.CuentaPorCobrarSolicitada;
import com.cloud_tecnoligical.nyxora_erp.event.DomainEventBus;
import com.cloud_tecnoligical.nyxora_erp.mapper.facturacion.FacturaDianMapper;
import com.cloud_tecnoligical.nyxora_erp.mapper.facturacion.FacturaLineaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.facturacion.FacturaDianR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.facturacion.FacturaLineaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.facturacion.FacturaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.facturacion.FacturaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.facturacion.ResolucionDianQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.MovimientoInventarioR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.FacturaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FacturaServiceImpl implements FacturaService {

    private final FacturaR2dbcRepository repo;
    private final FacturaLineaR2dbcRepository lineaRepo;
    private final FacturaDianR2dbcRepository dianRepo;
    private final FacturaQueryRepository queryRepo;
    private final ResolucionDianQueryRepository resolucionQuery;
    private final MovimientoInventarioR2dbcRepository movInvRepo;
    private final BodegaQueryRepository bodegaQuery;
    private final FacturaLineaMapper lineaMapper;
    private final FacturaDianMapper dianMapper;
    private final DomainEventBus eventBus;
    private final TransactionalOperator tx;

    public FacturaServiceImpl(FacturaR2dbcRepository repo, FacturaLineaR2dbcRepository lineaRepo,
                              FacturaDianR2dbcRepository dianRepo, FacturaQueryRepository queryRepo,
                              ResolucionDianQueryRepository resolucionQuery,
                              MovimientoInventarioR2dbcRepository movInvRepo, BodegaQueryRepository bodegaQuery,
                              FacturaLineaMapper lineaMapper, FacturaDianMapper dianMapper,
                              DomainEventBus eventBus, ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.lineaRepo = lineaRepo;
        this.dianRepo = dianRepo;
        this.queryRepo = queryRepo;
        this.resolucionQuery = resolucionQuery;
        this.movInvRepo = movInvRepo;
        this.bodegaQuery = bodegaQuery;
        this.lineaMapper = lineaMapper;
        this.dianMapper = dianMapper;
        this.eventBus = eventBus;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<FacturaResponseDto> create(CreateFacturaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                FacturaEntity f = new FacturaEntity();
                f.setEmpresa_id(t.getEmpresaId());
                aplicarCabecera(f, dto);
                f.setEstado("borrador");
                aplicarTotales(f, dto.getLineas());
                f.setActivo(true);
                f.setCreated_at(LocalDateTime.now());
                f.setUsuario_creacion(t.getUsuarioId());
                return repo.save(f)
                    .flatMap(saved -> insertarLineas(dto.getLineas(), saved.getId())
                        .then(cargarRespuesta(saved.getId(), t.getEmpresaId())))
                    .as(tx::transactional);
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateFacturaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(f -> {
                if (!"borrador".equals(f.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se edita una factura en borrador"));
                }
                return validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                    aplicarCabecera(f, dto);
                    aplicarTotales(f, dto.getLineas());
                    f.setUsuario_modificacion(t.getUsuarioId());
                    f.setUpdated_at(LocalDateTime.now());
                    return queryRepo.borrarLineas(f.getId())
                        .then(insertarLineas(dto.getLineas(), f.getId()))
                        .then(repo.save(f))
                        .thenReturn(true)
                        .as(tx::transactional);
                }));
            }));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(f -> {
                if (!"borrador".equals(f.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se elimina una factura en borrador"));
                }
                f.setDeleted_at(LocalDateTime.now());
                f.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(f).thenReturn(true);
            }));
    }

    @Override
    public Mono<FacturaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t -> cargarRespuesta(id, t.getEmpresaId()));
    }

    @Override
    public Mono<PageResponseDto<FacturaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<FacturaResponseDto> emitir(Long id, EmitirFacturaRequestDto params) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(f -> {
                if (!"borrador".equals(f.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se emite una factura en borrador"));
                }
                return queryRepo.listLineas(id).flatMap(lineas -> {
                    if (lineas.isEmpty()) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La factura no tiene líneas"));
                    }
                    Mono<FacturaEntity> flujo = asignarNumero(f, t.getEmpresaId())
                        .flatMap(fac -> generarSalidas(fac, lineas, t).thenReturn(fac))
                        .flatMap(fac -> {
                            fac.setEstado("emitida");
                            fac.setUsuario_modificacion(t.getUsuarioId());
                            fac.setUpdated_at(LocalDateTime.now());
                            return repo.save(fac);
                        });
                    return flujo.as(tx::transactional)
                        .doOnSuccess(fac -> {
                            publicarAsiento(params, fac, t);
                            publicarCartera(fac, t);
                        })
                        .then(cargarRespuesta(id, t.getEmpresaId()));
                });
            }));
    }

    @Override
    public Mono<Boolean> anular(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(f -> {
                if (!"borrador".equals(f.getEstado()) && !"emitida".equals(f.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se anula una factura en borrador o emitida"));
                }
                boolean reversaInventario = "emitida".equals(f.getEstado());
                f.setEstado("anulada");
                f.setUsuario_modificacion(t.getUsuarioId());
                f.setUpdated_at(LocalDateTime.now());
                Mono<Void> flujo = (reversaInventario
                        ? queryRepo.reversarMovimientosInventario(f.getId(), t.getEmpresaId(), t.getUsuarioId(), java.time.LocalDate.now()).then()
                        : Mono.<Void>empty())
                    .then(repo.save(f).then());
                return flujo.as(tx::transactional).thenReturn(true);
            }));
    }

    @Override
    public Mono<FacturaDianResponseDto> registrarDian(Long id, RegistrarFacturaDianRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(f -> {
                if (!"emitida".equals(f.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se registra la FE de una factura emitida"));
                }
                return queryRepo.findDianByFacturaId(id)
                    .flatMap(existing -> dianRepo.findById(existing.getId()))
                    .defaultIfEmpty(nuevaFacturaDian(id))
                    .flatMap(e -> {
                        e.setCufe(dto.getCufe());
                        e.setEstado_dian(dto.getEstadoDian());
                        e.setFecha_acuse(dto.getFechaAcuse());
                        e.setComentario_acuse(dto.getComentarioAcuse());
                        if (e.getId() != null) {
                            e.setUpdated_at(LocalDateTime.now());
                        }
                        return dianRepo.save(e);
                    })
                    .map(dianMapper::toResponseDto);
            }));
    }

    // ==================== helpers ====================

    private Mono<FacturaEntity> asignarNumero(FacturaEntity f, Long empresaId) {
        if (f.getResolucion_dian_id() == null) {
            if (f.getNumero() == null || f.getNumero().isBlank()) {
                return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                    "La factura requiere número o una resolución DIAN para numerar"));
            }
            return Mono.just(f);
        }
        return resolucionQuery.findActiveById(f.getResolucion_dian_id(), empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La resolución DIAN no existe")))
            .flatMap(res -> resolucionQuery.incrementarConsecutivo(f.getResolucion_dian_id(), empresaId)
                .flatMap(consecutivo -> {
                    if (res.getFacturaInicial() != null && consecutivo < res.getFacturaInicial()) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                            "El consecutivo está por debajo del rango de la resolución"));
                    }
                    if (res.getFacturaFinal() != null && consecutivo > res.getFacturaFinal()) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                            "La resolución DIAN agotó su rango de numeración"));
                    }
                    String prefijo = res.getPrefijo() != null ? res.getPrefijo() : "";
                    f.setNumero(prefijo + consecutivo);
                    return Mono.just(f);
                }));
    }

    /** Por cada línea con bodega genera un movimiento de inventario 'salida' (cantidad negativa). */
    private Mono<Void> generarSalidas(FacturaEntity f, List<FacturaLineaResponseDto> lineas, TenantInfo t) {
        return Flux.fromIterable(lineas).concatMap(l -> {
            Long bodegaId = nzId(l.getBodegaId()) != null ? nzId(l.getBodegaId()) : f.getBodega_id();
            if (bodegaId == null) {
                return Mono.empty();   // producto/servicio sin manejo de inventario
            }
            return queryRepo.costoPromedio(t.getEmpresaId(), bodegaId, l.getProductoId(), nzId(l.getProductoVarianteId()))
                .flatMap(costo -> movInvRepo.save(movimientoSalida(f, l, bodegaId, costo, t)).then());
        }).then();
    }

    private MovimientoInventarioEntity movimientoSalida(FacturaEntity f, FacturaLineaResponseDto l,
                                                        Long bodegaId, BigDecimal costo, TenantInfo t) {
        MovimientoInventarioEntity m = new MovimientoInventarioEntity();
        m.setEmpresa_id(t.getEmpresaId());
        m.setBodega_id(bodegaId);
        m.setProducto_id(l.getProductoId());
        m.setProducto_variante_id(nzId(l.getProductoVarianteId()));
        m.setLote_id(nzId(l.getLoteId()));
        m.setTipo("salida");
        m.setFecha(f.getFecha());
        m.setCantidad(l.getCantidad().negate());               // salida → negativa
        m.setCosto_unitario(nz(costo));
        BigDecimal subtotal = l.getCantidad().multiply(nz(costo));
        m.setSubtotal(subtotal);
        m.setTotal(subtotal);
        m.setCentro_costo_id(nzId(l.getCentroCostoId()) != null ? nzId(l.getCentroCostoId()) : f.getCentro_costo_id());
        m.setTercero_id(f.getCliente_id());
        m.setDescripcion("Salida por factura #" + f.getId());
        m.setOrigen_modulo("facturacion");
        m.setOrigen_id(f.getId());
        m.setCreated_at(LocalDateTime.now());
        m.setUsuario_creacion(t.getUsuarioId());
        return m;
    }

    /** Publica el asiento contable si se proveen cuenta cliente, cuenta ingreso y periodo. */
    private void publicarAsiento(EmitirFacturaRequestDto params, FacturaEntity f, TenantInfo t) {
        if (params == null || params.getCuentaClienteId() == null
                || params.getCuentaIngresoId() == null || params.getPeriodoContableId() == null) {
            return;
        }
        BigDecimal total = nz(f.getTotal());
        BigDecimal impuestos = nz(f.getImpuestos());
        if (total.signum() == 0) {
            return;
        }
        boolean ivaSeparado = impuestos.signum() > 0 && params.getCuentaImpuestoId() != null;

        CreateMovimientoContableDto debito = new CreateMovimientoContableDto();
        debito.setCuentaId(params.getCuentaClienteId());
        debito.setTerceroId(f.getCliente_id());
        debito.setDescripcion("CxC cliente - factura #" + f.getId());
        debito.setDebito(total);
        debito.setCredito(BigDecimal.ZERO);

        CreateMovimientoContableDto creditoIngreso = new CreateMovimientoContableDto();
        creditoIngreso.setCuentaId(params.getCuentaIngresoId());
        creditoIngreso.setDescripcion("Ingreso - factura #" + f.getId());
        creditoIngreso.setDebito(BigDecimal.ZERO);
        creditoIngreso.setCredito(ivaSeparado ? nz(f.getSubtotal()) : total);

        java.util.List<CreateMovimientoContableDto> movimientos = new java.util.ArrayList<>(List.of(debito, creditoIngreso));
        if (ivaSeparado) {
            CreateMovimientoContableDto creditoIva = new CreateMovimientoContableDto();
            creditoIva.setCuentaId(params.getCuentaImpuestoId());
            creditoIva.setDescripcion("IVA generado - factura #" + f.getId());
            creditoIva.setDebito(BigDecimal.ZERO);
            creditoIva.setCredito(impuestos);
            movimientos.add(creditoIva);
        }

        AsientoContableSolicitado evento = new AsientoContableSolicitado(
            t.getEmpresaId(), t.getUsuarioId(), t.getSedeId(),
            params.getPeriodoContableId(), f.getFecha(),
            "Factura de venta #" + f.getId(), "facturacion", f.getId(), movimientos);
        eventBus.publish(evento);
    }

    private void publicarCartera(FacturaEntity f, TenantInfo t) {
        eventBus.publish(new CuentaPorCobrarSolicitada(
            t.getEmpresaId(), t.getUsuarioId(), t.getSedeId(),
            f.getId(), f.getCliente_id(), f.getNumero(),
            f.getFecha(), f.getFecha_vencimiento(), nz(f.getTotal())));
    }

    private FacturaDianEntity nuevaFacturaDian(Long facturaId) {
        FacturaDianEntity e = new FacturaDianEntity();
        e.setFactura_id(facturaId);
        e.setCreated_at(LocalDateTime.now());
        return e;
    }

    private void aplicarCabecera(FacturaEntity f, CreateFacturaRequestDto dto) {
        f.setSede_id(dto.getSedeId());
        f.setVigencia_id(dto.getVigenciaId());
        f.setTipo_documento_id(dto.getTipoDocumentoId());
        f.setResolucion_dian_id(dto.getResolucionDianId());
        f.setNumero(dto.getNumero());
        f.setCliente_id(dto.getClienteId());
        f.setBodega_id(dto.getBodegaId());
        f.setCentro_costo_id(dto.getCentroCostoId());
        f.setCondicion_pago_id(dto.getCondicionPagoId());
        f.setFecha(dto.getFecha());
        f.setFecha_vencimiento(dto.getFechaVencimiento());
        f.setObservaciones(dto.getObservaciones());
    }

    private Mono<Void> insertarLineas(List<CreateFacturaLineaDto> lineas, Long facturaId) {
        return Flux.fromIterable(lineas)
            .map(dto -> {
                FacturaLineaEntity e = lineaMapper.toEntity(dto);
                e.setFactura_id(facturaId);
                BigDecimal[] tot = totalesLinea(dto);
                e.setSubtotal(tot[0]);
                e.setTotal(tot[1]);
                if (e.getDiscrimina_iva() == null) e.setDiscrimina_iva(false);
                e.setCreated_at(LocalDateTime.now());
                return e;
            })
            .collectList()
            .flatMapMany(lineaRepo::saveAll)
            .then();
    }

    private void aplicarTotales(FacturaEntity f, List<CreateFacturaLineaDto> lineas) {
        BigDecimal subtotal = BigDecimal.ZERO, descuento = BigDecimal.ZERO, impuestos = BigDecimal.ZERO, total = BigDecimal.ZERO;
        for (var l : lineas) {
            BigDecimal[] tot = totalesLinea(l);
            subtotal = subtotal.add(tot[0]);
            descuento = descuento.add(nz(l.getDescuentoValor()));
            impuestos = impuestos.add(nz(l.getValorImpuesto()));
            total = total.add(tot[1]);
        }
        f.setSubtotal(subtotal);
        f.setDescuento(descuento);
        f.setImpuestos(impuestos);
        f.setTotal(total);
    }

    /** [subtotal, total] de una línea: subtotal = cantidad·unitario − descuento; total = subtotal + impuesto. */
    private BigDecimal[] totalesLinea(CreateFacturaLineaDto l) {
        BigDecimal bruto = l.getCantidad().multiply(nz(l.getValorUnitario()));
        BigDecimal subtotal = bruto.subtract(nz(l.getDescuentoValor()));
        BigDecimal total = subtotal.add(nz(l.getValorImpuesto()));
        return new BigDecimal[]{subtotal, total};
    }

    private Mono<Void> validarReferencias(CreateFacturaRequestDto dto, Long empresaId) {
        Set<Long> productos = dto.getLineas().stream()
            .map(CreateFacturaLineaDto::getProductoId).filter(Objects::nonNull).collect(Collectors.toSet());
        Mono<Void> vCliente = queryRepo.terceroExisteEnEmpresa(dto.getClienteId(), empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El cliente no existe")));
        Mono<Void> vBodega = dto.getBodegaId() == null ? Mono.empty()
            : bodegaQuery.existsActivaEnEmpresa(dto.getBodegaId(), empresaId)
                .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La bodega no existe")));
        Mono<Void> vProd = productos.isEmpty() ? Mono.empty()
            : queryRepo.countProductosEnEmpresa(productos, empresaId)
                .flatMap(n -> n == productos.size() ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Algún producto no existe en la empresa")));
        return vCliente.then(vBodega).then(vProd);
    }

    private Mono<FacturaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Factura no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Factura no encontrada"));
                }
                return Mono.just(e);
            });
    }

    private Mono<FacturaResponseDto> cargarRespuesta(Long id, Long empresaId) {
        return queryRepo.findActiveById(id, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Factura no encontrada")))
            .flatMap(resp -> queryRepo.listLineas(id).map(ls -> { resp.setLineas(ls); return resp; }));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    /**
     * Normaliza un id de FK opcional: trata 0 como ausente (null). Las líneas se leen con
     * MapperRepository, que convierte los NULL numéricos en 0; insertar 0 violaría las FKs.
     */
    private static Long nzId(Long v) {
        return (v == null || v == 0L) ? null : v;
    }
}
