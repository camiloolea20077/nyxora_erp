package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateMovimientoInventarioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.KardexItemDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MovimientoInventarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.TrasladoInventarioDto;
import com.cloud_tecnoligical.nyxora_erp.entity.MovimientoInventarioEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.inventario.MovimientoInventarioMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.BodegaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.MovimientoInventarioQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.MovimientoInventarioR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.inventario.UbicacionQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.producto.ProductoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.MovimientoInventarioService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioR2dbcRepository repo;
    private final MovimientoInventarioQueryRepository queryRepo;
    private final MovimientoInventarioMapper mapper;
    private final BodegaQueryRepository bodegaQuery;
    private final ProductoQueryRepository productoQuery;
    private final UbicacionQueryRepository ubicacionQuery;
    private final TransactionalOperator tx;

    public MovimientoInventarioServiceImpl(MovimientoInventarioR2dbcRepository repo,
                                           MovimientoInventarioQueryRepository queryRepo,
                                           MovimientoInventarioMapper mapper,
                                           BodegaQueryRepository bodegaQuery,
                                           ProductoQueryRepository productoQuery,
                                           UbicacionQueryRepository ubicacionQuery,
                                           ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
        this.bodegaQuery = bodegaQuery;
        this.productoQuery = productoQuery;
        this.ubicacionQuery = ubicacionQuery;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<MovimientoInventarioResponseDto> registrar(CreateMovimientoInventarioDto dto) {
        return TenantContext.get().flatMap(t ->
            validarReferencias(t.getEmpresaId(), dto.getBodegaId(), dto.getProductoId(),
                    dto.getUbicacionId(), dto.getLoteId(), dto.getProductoVarianteId())
                .then(Mono.defer(() -> {
                    MovimientoInventarioEntity e = mapper.toEntity(dto);
                    e.setEmpresa_id(t.getEmpresaId());
                    e.setUsuario_creacion(t.getUsuarioId());
                    e.setCreated_at(LocalDateTime.now());
                    e.setCantidad(cantidadConSigno(dto.getTipo(), dto.getCantidad()));
                    aplicarCostos(e);
                    return repo.save(e).map(mapper::toResponseDto);
                })));
    }

    @Override
    public Mono<List<MovimientoInventarioResponseDto>> traslado(TrasladoInventarioDto dto) {
        return TenantContext.get().flatMap(t -> {
            if (dto.getBodegaOrigenId().equals(dto.getBodegaDestinoId())) {
                return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La bodega origen y destino no pueden ser la misma"));
            }
            Long e = t.getEmpresaId();
            return validarBodega(dto.getBodegaOrigenId(), e)
                .then(validarBodega(dto.getBodegaDestinoId(), e))
                .then(validarProducto(dto.getProductoId(), e))
                .then(Mono.defer(() -> {
                    MovimientoInventarioEntity origen = trasladoRow(dto, t,
                        dto.getBodegaOrigenId(), dto.getUbicacionOrigenId(), dto.getCantidad().negate());
                    MovimientoInventarioEntity destino = trasladoRow(dto, t,
                        dto.getBodegaDestinoId(), dto.getUbicacionDestinoId(), dto.getCantidad());
                    return repo.save(origen)
                        .flatMap(o -> repo.save(destino)
                            .map(d -> List.of(mapper.toResponseDto(o), mapper.toResponseDto(d))))
                        .as(tx::transactional);
                }));
        });
    }

    @Override
    public Mono<MovimientoInventarioResponseDto> reversar(Long id) {
        return TenantContext.get().flatMap(t ->
            repo.findById(id)
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Movimiento no encontrado")))
                .flatMap(orig -> {
                    if (!orig.getEmpresa_id().equals(t.getEmpresaId())) {
                        return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));
                    }
                    MovimientoInventarioEntity inv = new MovimientoInventarioEntity();
                    inv.setEmpresa_id(orig.getEmpresa_id());
                    inv.setBodega_id(orig.getBodega_id());
                    inv.setUbicacion_id(orig.getUbicacion_id());
                    inv.setProducto_id(orig.getProducto_id());
                    inv.setProducto_variante_id(orig.getProducto_variante_id());
                    inv.setLote_id(orig.getLote_id());
                    inv.setTipo("ajuste");
                    inv.setFecha(java.time.LocalDate.now());
                    inv.setCantidad(orig.getCantidad().negate());
                    inv.setCosto_unitario(orig.getCosto_unitario());
                    inv.setCentro_costo_id(orig.getCentro_costo_id());
                    inv.setTercero_id(orig.getTercero_id());
                    inv.setDescripcion("Reversa de movimiento #" + id);
                    inv.setOrigen_modulo("inventario");
                    inv.setOrigen_id(id);
                    inv.setCreated_at(LocalDateTime.now());
                    inv.setUsuario_creacion(t.getUsuarioId());
                    aplicarCostos(inv);
                    return repo.save(inv).map(mapper::toResponseDto);
                }));
    }

    @Override
    public Mono<MovimientoInventarioResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"))));
    }

    @Override
    public Mono<List<KardexItemDto>> kardex(Long productoId, Long bodegaId) {
        return TenantContext.get().flatMap(t -> queryRepo.kardex(productoId, bodegaId, t.getEmpresaId()));
    }

    // ==================== helpers ====================

    private MovimientoInventarioEntity trasladoRow(TrasladoInventarioDto dto, TenantInfo t,
                                                   Long bodegaId, Long ubicacionId, BigDecimal cantidad) {
        MovimientoInventarioEntity e = new MovimientoInventarioEntity();
        e.setEmpresa_id(t.getEmpresaId());
        e.setBodega_id(bodegaId);
        e.setUbicacion_id(ubicacionId);
        e.setProducto_id(dto.getProductoId());
        e.setProducto_variante_id(dto.getProductoVarianteId());
        e.setLote_id(dto.getLoteId());
        e.setTipo("traslado");
        e.setFecha(dto.getFecha());
        e.setCantidad(cantidad);
        e.setCosto_unitario(nz(dto.getCostoUnitario()));
        e.setDescripcion(dto.getDescripcion());
        e.setOrigen_modulo("inventario");
        e.setCreated_at(LocalDateTime.now());
        e.setUsuario_creacion(t.getUsuarioId());
        aplicarCostos(e);
        return e;
    }

    private BigDecimal cantidadConSigno(String tipo, BigDecimal cantidad) {
        BigDecimal c = nz(cantidad);
        switch (tipo) {
            case "entrada" -> {
                if (c.signum() <= 0) {
                    throw new GlobalException(HttpStatus.BAD_REQUEST, "La cantidad de entrada debe ser positiva");
                }
                return c;
            }
            case "salida" -> {
                if (c.signum() <= 0) {
                    throw new GlobalException(HttpStatus.BAD_REQUEST, "La cantidad de salida debe ser positiva");
                }
                return c.negate();
            }
            case "ajuste" -> {
                if (c.signum() == 0) {
                    throw new GlobalException(HttpStatus.BAD_REQUEST, "El ajuste no puede ser cero");
                }
                return c;
            }
            default -> throw new GlobalException(HttpStatus.BAD_REQUEST, "Tipo de movimiento inválido");
        }
    }

    /** costo_unitario es NOT NULL; calcula subtotal/total best-effort sobre la magnitud. */
    private void aplicarCostos(MovimientoInventarioEntity e) {
        if (e.getCosto_unitario() == null) e.setCosto_unitario(BigDecimal.ZERO);
        BigDecimal magnitud = e.getCantidad().abs();
        BigDecimal subtotal = magnitud.multiply(e.getCosto_unitario());
        e.setSubtotal(subtotal);
        BigDecimal total = subtotal.subtract(nz(e.getDescuento_valor())).add(nz(e.getImpuesto_valor()));
        e.setTotal(total);
    }

    private Mono<Void> validarReferencias(Long empresaId, Long bodegaId, Long productoId,
                                          Long ubicacionId, Long loteId, Long varianteId) {
        return validarBodega(bodegaId, empresaId)
            .then(validarProducto(productoId, empresaId))
            .then(ubicacionId == null ? Mono.empty()
                : ubicacionQuery.existsActivaEnEmpresa(ubicacionId, empresaId)
                    .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                        : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La ubicación no existe"))))
            .then(loteId == null ? Mono.empty()
                : queryRepo.loteExisteEnEmpresa(loteId, empresaId)
                    .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                        : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El lote no existe"))))
            .then(varianteId == null ? Mono.empty()
                : queryRepo.varianteExisteEnEmpresa(varianteId, empresaId)
                    .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                        : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La variante no existe"))));
    }

    private Mono<Void> validarBodega(Long bodegaId, Long empresaId) {
        return bodegaQuery.existsActivaEnEmpresa(bodegaId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La bodega no existe")));
    }

    private Mono<Void> validarProducto(Long productoId, Long empresaId) {
        return productoQuery.existsActivoEnEmpresa(productoId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El producto no existe")));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
