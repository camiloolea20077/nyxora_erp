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
import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateOrdenCompraLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateOrdenCompraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.UpdateOrdenCompraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.OrdenCompraEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.OrdenCompraLineaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.compras.OrdenCompraLineaMapper;
import com.cloud_tecnoligical.nyxora_erp.mapper.compras.OrdenCompraMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.compras.OrdenCompraLineaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.compras.OrdenCompraQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.compras.OrdenCompraR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.OrdenCompraService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraR2dbcRepository repo;
    private final OrdenCompraLineaR2dbcRepository lineaRepo;
    private final OrdenCompraQueryRepository queryRepo;
    private final BodegaQueryRepository bodegaQuery;
    private final OrdenCompraMapper mapper;
    private final OrdenCompraLineaMapper lineaMapper;
    private final TransactionalOperator tx;

    public OrdenCompraServiceImpl(OrdenCompraR2dbcRepository repo, OrdenCompraLineaR2dbcRepository lineaRepo,
                                  OrdenCompraQueryRepository queryRepo, BodegaQueryRepository bodegaQuery,
                                  OrdenCompraMapper mapper, OrdenCompraLineaMapper lineaMapper,
                                  ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.lineaRepo = lineaRepo;
        this.queryRepo = queryRepo;
        this.bodegaQuery = bodegaQuery;
        this.mapper = mapper;
        this.lineaMapper = lineaMapper;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<OrdenCompraResponseDto> create(CreateOrdenCompraRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                OrdenCompraEntity h = new OrdenCompraEntity();
                h.setEmpresa_id(t.getEmpresaId());
                h.setSede_id(dto.getSedeId());
                h.setVigencia_id(dto.getVigenciaId());
                h.setTipo_documento_id(dto.getTipoDocumentoId());
                h.setNumero(dto.getNumero());
                h.setProveedor_id(dto.getProveedorId());
                h.setBodega_id(dto.getBodegaId());
                h.setCentro_costo_id(dto.getCentroCostoId());
                h.setCondicion_pago_id(dto.getCondicionPagoId());
                h.setFecha(dto.getFecha());
                h.setFecha_entrega(dto.getFechaEntrega());
                h.setObservaciones(dto.getObservaciones());
                h.setEstado("borrador");
                aplicarTotales(h, dto.getLineas());
                h.setActivo(true);
                h.setCreated_at(LocalDateTime.now());
                h.setUsuario_creacion(t.getUsuarioId());
                return repo.save(h)
                    .flatMap(saved -> insertarLineas(dto.getLineas(), saved.getId())
                        .then(cargarRespuesta(saved.getId(), t.getEmpresaId())))
                    .as(tx::transactional);
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateOrdenCompraRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(h -> {
                if (!"borrador".equals(h.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se edita una orden en borrador"));
                }
                return validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                    h.setSede_id(dto.getSedeId());
                    h.setVigencia_id(dto.getVigenciaId());
                    h.setTipo_documento_id(dto.getTipoDocumentoId());
                    h.setNumero(dto.getNumero());
                    h.setProveedor_id(dto.getProveedorId());
                    h.setBodega_id(dto.getBodegaId());
                    h.setCentro_costo_id(dto.getCentroCostoId());
                    h.setCondicion_pago_id(dto.getCondicionPagoId());
                    h.setFecha(dto.getFecha());
                    h.setFecha_entrega(dto.getFechaEntrega());
                    h.setObservaciones(dto.getObservaciones());
                    aplicarTotales(h, dto.getLineas());
                    h.setUsuario_modificacion(t.getUsuarioId());
                    h.setUpdated_at(LocalDateTime.now());
                    return queryRepo.borrarLineas(h.getId())
                        .then(insertarLineas(dto.getLineas(), h.getId()))
                        .then(repo.save(h))
                        .thenReturn(true)
                        .as(tx::transactional);
                }));
            }));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(h -> {
                if (!"borrador".equals(h.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se elimina una orden en borrador"));
                }
                h.setDeleted_at(LocalDateTime.now());
                h.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(h).thenReturn(true);
            }));
    }

    @Override
    public Mono<OrdenCompraResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t -> cargarRespuesta(id, t.getEmpresaId()));
    }

    @Override
    public Mono<PageResponseDto<OrdenCompraTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<Boolean> aprobar(Long id) {
        return cambiarEstado(id, "borrador", "aprobada", "Solo se aprueba una orden en borrador");
    }

    @Override
    public Mono<Boolean> anular(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(h -> {
                if (!"borrador".equals(h.getEstado()) && !"aprobada".equals(h.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se anula una orden en borrador o aprobada"));
                }
                h.setEstado("anulada");
                h.setUsuario_modificacion(t.getUsuarioId());
                h.setUpdated_at(LocalDateTime.now());
                return repo.save(h).thenReturn(true);
            }));
    }

    // ==================== helpers ====================

    private Mono<Boolean> cambiarEstado(Long id, String desde, String hacia, String msgError) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(h -> {
                if (!desde.equals(h.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, msgError));
                }
                h.setEstado(hacia);
                h.setUsuario_modificacion(t.getUsuarioId());
                h.setUpdated_at(LocalDateTime.now());
                return repo.save(h).thenReturn(true);
            }));
    }

    private Mono<Void> insertarLineas(List<CreateOrdenCompraLineaDto> lineas, Long ordenId) {
        return Flux.fromIterable(lineas)
            .map(dto -> {
                OrdenCompraLineaEntity e = lineaMapper.toEntity(dto);
                e.setOrden_compra_id(ordenId);
                BigDecimal[] tot = totalesLinea(dto);
                e.setSubtotal(tot[0]);
                e.setTotal(tot[1]);
                e.setCantidad_recibida(BigDecimal.ZERO);
                e.setCantidad_pendiente(dto.getCantidad());
                e.setCreated_at(LocalDateTime.now());
                return e;
            })
            .collectList()
            .flatMapMany(lineaRepo::saveAll)
            .then();
    }

    private void aplicarTotales(OrdenCompraEntity h, List<CreateOrdenCompraLineaDto> lineas) {
        BigDecimal subtotal = BigDecimal.ZERO, descuento = BigDecimal.ZERO, impuestos = BigDecimal.ZERO, total = BigDecimal.ZERO;
        for (var l : lineas) {
            BigDecimal[] tot = totalesLinea(l);
            subtotal = subtotal.add(tot[0]);
            descuento = descuento.add(nz(l.getDescuentoValor()));
            impuestos = impuestos.add(nz(l.getImpuestoValor()));
            total = total.add(tot[1]);
        }
        h.setSubtotal(subtotal);
        h.setDescuento(descuento);
        h.setImpuestos(impuestos);
        h.setTotal(total);
    }

    /** [subtotal, total] de una línea: subtotal = cantidad·unitario − descuento; total = subtotal + impuesto. */
    private BigDecimal[] totalesLinea(CreateOrdenCompraLineaDto l) {
        BigDecimal bruto = l.getCantidad().multiply(nz(l.getValorUnitario()));
        BigDecimal subtotal = bruto.subtract(nz(l.getDescuentoValor()));
        BigDecimal total = subtotal.add(nz(l.getImpuestoValor()));
        return new BigDecimal[]{subtotal, total};
    }

    private Mono<Void> validarReferencias(CreateOrdenCompraRequestDto dto, Long empresaId) {
        Set<Long> productos = dto.getLineas().stream()
            .map(CreateOrdenCompraLineaDto::getProductoId).filter(Objects::nonNull).collect(Collectors.toSet());
        Mono<Void> vProv = queryRepo.terceroExisteEnEmpresa(dto.getProveedorId(), empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El proveedor no existe")));
        Mono<Void> vBodega = dto.getBodegaId() == null ? Mono.empty()
            : bodegaQuery.existsActivaEnEmpresa(dto.getBodegaId(), empresaId)
                .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La bodega no existe")));
        Mono<Void> vProd = productos.isEmpty() ? Mono.empty()
            : queryRepo.countProductosEnEmpresa(productos, empresaId)
                .flatMap(n -> n == productos.size() ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Algún producto no existe en la empresa")));
        return vProv.then(vBodega).then(vProd);
    }

    private Mono<OrdenCompraEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Orden de compra no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Orden de compra no encontrada"));
                }
                return Mono.just(e);
            });
    }

    private Mono<OrdenCompraResponseDto> cargarRespuesta(Long id, Long empresaId) {
        return queryRepo.findActiveById(id, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Orden de compra no encontrada")))
            .flatMap(resp -> queryRepo.listLineas(id).map(ls -> { resp.setLineas(ls); return resp; }));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
