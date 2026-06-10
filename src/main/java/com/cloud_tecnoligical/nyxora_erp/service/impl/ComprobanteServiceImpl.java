package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.ComprobanteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.ComprobanteTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateComprobanteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateMovimientoContableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ComprobanteEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.MovimientoContableEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.contabilidad.ComprobanteMapper;
import com.cloud_tecnoligical.nyxora_erp.mapper.contabilidad.MovimientoContableMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.ComprobanteQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.ComprobanteR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.MovimientoContableR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contabilidad.PeriodoContableQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ComprobanteService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ComprobanteServiceImpl implements ComprobanteService {

    private final ComprobanteR2dbcRepository comprobanteRepo;
    private final MovimientoContableR2dbcRepository movimientoRepo;
    private final ComprobanteQueryRepository queryRepo;
    private final PeriodoContableQueryRepository periodoQuery;
    private final ComprobanteMapper comprobanteMapper;
    private final MovimientoContableMapper movimientoMapper;
    private final TransactionalOperator tx;

    public ComprobanteServiceImpl(ComprobanteR2dbcRepository comprobanteRepo,
                                  MovimientoContableR2dbcRepository movimientoRepo,
                                  ComprobanteQueryRepository queryRepo,
                                  PeriodoContableQueryRepository periodoQuery,
                                  ComprobanteMapper comprobanteMapper,
                                  MovimientoContableMapper movimientoMapper,
                                  ReactiveTransactionManager txManager) {
        this.comprobanteRepo = comprobanteRepo;
        this.movimientoRepo = movimientoRepo;
        this.queryRepo = queryRepo;
        this.periodoQuery = periodoQuery;
        this.comprobanteMapper = comprobanteMapper;
        this.movimientoMapper = movimientoMapper;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<ComprobanteResponseDto> create(CreateComprobanteRequestDto dto) {
        return TenantContext.get().flatMap(t -> {
            BigDecimal[] totales = normalizarYValidarLineas(dto.getMovimientos());
            return validarReferencias(dto, t.getEmpresaId())
                .then(Mono.defer(() -> {
                    ComprobanteEntity header = new ComprobanteEntity();
                    header.setEmpresa_id(t.getEmpresaId());
                    header.setPeriodo_contable_id(dto.getPeriodoContableId());
                    header.setTipo_documento_id(dto.getTipoDocumentoId());
                    header.setNumero(dto.getNumero());
                    header.setFecha(dto.getFecha());
                    header.setDescripcion(dto.getDescripcion());
                    header.setEstado("borrador");
                    header.setTotal_debito(totales[0]);
                    header.setTotal_credito(totales[1]);
                    header.setOrigen_modulo(dto.getOrigenModulo());
                    header.setOrigen_id(dto.getOrigenId());
                    header.setActivo(true);
                    header.setCreated_at(LocalDateTime.now());
                    header.setUsuario_creacion(t.getUsuarioId());

                    return comprobanteRepo.save(header)
                        .flatMap(saved -> insertarMovimientos(dto.getMovimientos(), saved.getId(), t.getEmpresaId(), t.getUsuarioId())
                            .then(toResponseConMovimientos(saved)))
                        .as(tx::transactional);
                }));
        });
    }

    @Override
    public Mono<ComprobanteResponseDto> crearYConfirmar(CreateComprobanteRequestDto dto) {
        return create(dto).flatMap(c -> confirmar(c.getId()).then(cargarRespuesta(c.getId())));
    }

    @Override
    public Mono<ComprobanteResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t -> cargarRespuesta(id, t.getEmpresaId()));
    }

    @Override
    public Mono<PageResponseDto<ComprobanteTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<Boolean> confirmar(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(header -> {
                if (!"borrador".equals(header.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se confirman comprobantes en borrador"));
                }
                return exigirPeriodoAbierto(header.getPeriodo_contable_id(), t.getEmpresaId())
                    .then(queryRepo.listMovimientos(id))
                    .flatMap(movs -> {
                        BigDecimal deb = movs.stream().map(m -> nz(m.getDebito())).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal cre = movs.stream().map(m -> nz(m.getCredito())).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (deb.compareTo(cre) != 0 || deb.signum() == 0) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El comprobante no está balanceado (débito ≠ crédito)"));
                        }
                        header.setEstado("confirmado");
                        header.setTotal_debito(deb);
                        header.setTotal_credito(cre);
                        header.setUsuario_modificacion(t.getUsuarioId());
                        header.setUpdated_at(LocalDateTime.now());
                        return comprobanteRepo.save(header).thenReturn(true);
                    });
            }));
    }

    @Override
    public Mono<Boolean> reversar(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(original -> {
                if (!"confirmado".equals(original.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se reversan comprobantes confirmados"));
                }
                return exigirPeriodoAbierto(original.getPeriodo_contable_id(), t.getEmpresaId())
                    .then(queryRepo.listMovimientos(id))
                    .flatMap(movs -> {
                        ComprobanteEntity rev = new ComprobanteEntity();
                        rev.setEmpresa_id(t.getEmpresaId());
                        rev.setPeriodo_contable_id(original.getPeriodo_contable_id());
                        rev.setTipo_documento_id(original.getTipo_documento_id());
                        rev.setNumero(original.getNumero() != null ? original.getNumero() + "-REV" : null);
                        rev.setFecha(LocalDate.now());
                        rev.setDescripcion("Reversa de comprobante #" + id);
                        rev.setEstado("confirmado");
                        rev.setTotal_debito(original.getTotal_credito());
                        rev.setTotal_credito(original.getTotal_debito());
                        rev.setOrigen_modulo("contabilidad");
                        rev.setOrigen_id(id);
                        rev.setActivo(true);
                        rev.setCreated_at(LocalDateTime.now());
                        rev.setUsuario_creacion(t.getUsuarioId());

                        Mono<Void> flujo = comprobanteRepo.save(rev).flatMap(revSaved -> {
                            List<MovimientoContableEntity> inversos = new ArrayList<>();
                            for (var m : movs) {
                                MovimientoContableEntity e = new MovimientoContableEntity();
                                e.setEmpresa_id(t.getEmpresaId());
                                e.setComprobante_id(revSaved.getId());
                                e.setCuenta_id(m.getCuentaId());
                                e.setTercero_id(m.getTerceroId());
                                e.setCentro_costo_id(m.getCentroCostoId());
                                e.setProyecto_id(m.getProyectoId());
                                e.setRecurso_id(m.getRecursoId());
                                e.setDescripcion("Reversa: " + (m.getDescripcion() != null ? m.getDescripcion() : ""));
                                e.setDebito(nz(m.getCredito()));   // swap
                                e.setCredito(nz(m.getDebito()));   // swap
                                e.setValor_base(m.getValorBase());
                                e.setImpuesto_id(m.getImpuestoId());
                                e.setPorcentaje_impuesto(m.getPorcentajeImpuesto());
                                e.setValor_trm(m.getValorTrm());
                                e.setValor_dolar(m.getValorDolar());
                                e.setCreated_at(LocalDateTime.now());
                                e.setUsuario_creacion(t.getUsuarioId());
                                inversos.add(e);
                            }
                            original.setEstado("reversado");
                            original.setUsuario_modificacion(t.getUsuarioId());
                            original.setUpdated_at(LocalDateTime.now());
                            return movimientoRepo.saveAll(inversos).then(comprobanteRepo.save(original)).then();
                        });
                        return flujo.as(tx::transactional).thenReturn(true);
                    });
            }));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(header -> {
                if (!"borrador".equals(header.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se pueden eliminar comprobantes en borrador"));
                }
                header.setDeleted_at(LocalDateTime.now());
                header.setUsuario_modificacion(t.getUsuarioId());
                return comprobanteRepo.save(header).thenReturn(true);
            }));
    }

    // ==================== helpers ====================

    private Mono<Void> insertarMovimientos(List<CreateMovimientoContableDto> lineas, Long comprobanteId,
                                           Long empresaId, Long usuarioId) {
        return Flux.fromIterable(lineas)
            .map(dto -> {
                MovimientoContableEntity e = movimientoMapper.toEntity(dto);
                e.setComprobante_id(comprobanteId);
                e.setEmpresa_id(empresaId);
                e.setDebito(nz(e.getDebito()));
                e.setCredito(nz(e.getCredito()));
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(usuarioId);
                return e;
            })
            .collectList()
            .flatMapMany(movimientoRepo::saveAll)
            .then();
    }

    /** Normaliza débito/crédito (null→0), valida débito XOR crédito por línea y partida doble global. */
    private BigDecimal[] normalizarYValidarLineas(List<CreateMovimientoContableDto> lineas) {
        BigDecimal totalDeb = BigDecimal.ZERO;
        BigDecimal totalCre = BigDecimal.ZERO;
        for (var l : lineas) {
            BigDecimal d = nz(l.getDebito());
            BigDecimal c = nz(l.getCredito());
            if (d.signum() < 0 || c.signum() < 0) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "Débito y crédito no pueden ser negativos");
            }
            boolean tieneDeb = d.signum() > 0;
            boolean tieneCre = c.signum() > 0;
            if (tieneDeb == tieneCre) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "Cada movimiento debe tener débito O crédito (no ambos ni ninguno)");
            }
            l.setDebito(d);
            l.setCredito(c);
            totalDeb = totalDeb.add(d);
            totalCre = totalCre.add(c);
        }
        if (totalDeb.compareTo(totalCre) != 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "El comprobante no está balanceado (débito ≠ crédito)");
        }
        if (totalDeb.signum() == 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "El comprobante no puede tener total cero");
        }
        return new BigDecimal[]{totalDeb, totalCre};
    }

    /** Valida periodo, cuentas imputables y dimensiones (tercero/centro/proyecto/impuesto) de la empresa. */
    private Mono<Void> validarReferencias(CreateComprobanteRequestDto dto, Long empresaId) {
        return periodoQuery.findEstado(dto.getPeriodoContableId(), empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El periodo contable no existe")))
            .then(Mono.defer(() -> {
                Set<Long> cuentas = idset(dto.getMovimientos(), CreateMovimientoContableDto::getCuentaId);
                Set<Long> terceros = idset(dto.getMovimientos(), CreateMovimientoContableDto::getTerceroId);
                Set<Long> centros = idset(dto.getMovimientos(), CreateMovimientoContableDto::getCentroCostoId);
                Set<Long> proyectos = idset(dto.getMovimientos(), CreateMovimientoContableDto::getProyectoId);
                Set<Long> impuestos = idset(dto.getMovimientos(), CreateMovimientoContableDto::getImpuestoId);

                Mono<Void> vCuentas = queryRepo.countCuentasImputables(cuentas, empresaId)
                    .flatMap(n -> n == cuentas.size() ? Mono.<Void>empty()
                        : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Alguna cuenta no existe o no admite movimientos (maneja_movimiento)")));

                return vCuentas
                    .then(validarDimension(terceros, queryRepo.countTerceros(terceros, empresaId), "tercero"))
                    .then(validarDimension(centros, queryRepo.countCentrosCosto(centros, empresaId), "centro de costo"))
                    .then(validarDimension(proyectos, queryRepo.countProyectos(proyectos, empresaId), "proyecto"))
                    .then(validarDimension(impuestos, queryRepo.countImpuestos(impuestos, empresaId), "impuesto"));
            }));
    }

    private Mono<Void> validarDimension(Set<Long> ids, Mono<Long> conteo, String nombre) {
        if (ids.isEmpty()) {
            return Mono.empty();
        }
        return conteo.flatMap(n -> n == ids.size() ? Mono.<Void>empty()
            : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Algún " + nombre + " referenciado no es de la empresa")));
    }

    private Set<Long> idset(List<CreateMovimientoContableDto> lineas,
                            java.util.function.Function<CreateMovimientoContableDto, Long> f) {
        return lineas.stream().map(f).filter(java.util.Objects::nonNull).collect(Collectors.toSet());
    }

    private Mono<Void> exigirPeriodoAbierto(Long periodoId, Long empresaId) {
        return periodoQuery.findEstado(periodoId, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El periodo contable no existe")))
            .flatMap(estado -> "abierto".equals(estado) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El periodo contable está cerrado")));
    }

    private Mono<ComprobanteEntity> cargarEntidad(Long id, Long empresaId) {
        return comprobanteRepo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Comprobante no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Comprobante no encontrado"));
                }
                return Mono.just(e);
            });
    }

    private Mono<ComprobanteResponseDto> cargarRespuesta(Long id) {
        return TenantContext.get().flatMap(t -> cargarRespuesta(id, t.getEmpresaId()));
    }

    private Mono<ComprobanteResponseDto> cargarRespuesta(Long id, Long empresaId) {
        return queryRepo.findActiveById(id, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Comprobante no encontrado")))
            .flatMap(resp -> queryRepo.listMovimientos(id).map(m -> { resp.setMovimientos(m); return resp; }));
    }

    private Mono<ComprobanteResponseDto> toResponseConMovimientos(ComprobanteEntity header) {
        ComprobanteResponseDto resp = comprobanteMapper.toResponseDto(header);
        return queryRepo.listMovimientos(header.getId()).map(m -> { resp.setMovimientos(m); return resp; });
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
