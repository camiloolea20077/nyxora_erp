package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateMovimientoContableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.AportePilaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ContabilizarNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateLiquidacionNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionDetalleDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidarNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateLiquidacionNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.AportePilaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.LiquidacionNominaDetalleEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.LiquidacionNominaEntity;
import com.cloud_tecnoligical.nyxora_erp.event.AsientoContableSolicitado;
import com.cloud_tecnoligical.nyxora_erp.event.DomainEventBus;
import com.cloud_tecnoligical.nyxora_erp.mapper.nomina.LiquidacionNominaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.AportePilaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.ConceptoNominaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.GrupoNominaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.LiquidacionNominaDetalleR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.LiquidacionNominaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.LiquidacionNominaQueryRepository.NovedadRow;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.LiquidacionNominaQueryRepository.ResumenClase;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.LiquidacionNominaQueryRepository.VinculacionRow;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.LiquidacionNominaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.LiquidacionNominaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class LiquidacionNominaServiceImpl implements LiquidacionNominaService {

    // Porcentajes PILA (empleado/patrono) — parametrizables a futuro.
    private static final BigDecimal SALUD_EMP = new BigDecimal("0.04");
    private static final BigDecimal SALUD_PAT = new BigDecimal("0.085");
    private static final BigDecimal PENSION_EMP = new BigDecimal("0.04");
    private static final BigDecimal PENSION_PAT = new BigDecimal("0.12");

    private final LiquidacionNominaR2dbcRepository repo;
    private final LiquidacionNominaDetalleR2dbcRepository detalleRepo;
    private final AportePilaR2dbcRepository pilaRepo;
    private final LiquidacionNominaQueryRepository queryRepo;
    private final LiquidacionNominaMapper mapper;
    private final GrupoNominaQueryRepository grupoQueryRepository;
    private final ConceptoNominaQueryRepository conceptoQueryRepository;
    private final DomainEventBus eventBus;
    private final TransactionalOperator tx;

    public LiquidacionNominaServiceImpl(LiquidacionNominaR2dbcRepository repo,
            LiquidacionNominaDetalleR2dbcRepository detalleRepo, AportePilaR2dbcRepository pilaRepo,
            LiquidacionNominaQueryRepository queryRepo, LiquidacionNominaMapper mapper,
            GrupoNominaQueryRepository grupoQueryRepository, ConceptoNominaQueryRepository conceptoQueryRepository,
            DomainEventBus eventBus, ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.detalleRepo = detalleRepo;
        this.pilaRepo = pilaRepo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
        this.grupoQueryRepository = grupoQueryRepository;
        this.conceptoQueryRepository = conceptoQueryRepository;
        this.eventBus = eventBus;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<LiquidacionNominaResponseDto> create(CreateLiquidacionNominaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarGrupo(dto.getGrupoNominaId(), t.getEmpresaId()).then(Mono.defer(() -> {
                LiquidacionNominaEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setEstado("abierto");
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateLiquidacionNominaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e -> {
                if (!"abierto".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se edita una nómina abierta"));
                }
                return validarGrupo(dto.getGrupoNominaId(), t.getEmpresaId()).then(Mono.defer(() -> {
                    e.setGrupo_nomina_id(dto.getGrupoNominaId());
                    e.setAnio(dto.getAnio());
                    e.setMes(dto.getMes());
                    e.setPeriodo(dto.getPeriodo());
                    e.setFecha(dto.getFecha());
                    e.setUpdated_at(LocalDateTime.now());
                    e.setUsuario_modificacion(t.getUsuarioId());
                    return repo.save(e).thenReturn(true);
                }));
            }));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                if (!"abierto".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se elimina una nómina abierta"));
                }
                e.setDeleted_at(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<LiquidacionNominaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Liquidación no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<LiquidacionNominaTableDto>> list(PageableDto<?> request, Long grupoNominaId) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId(), grupoNominaId));
    }

    // ==================== Liquidar ====================
    @Override
    public Mono<LiquidacionNominaResponseDto> liquidar(Long id, LiquidarNominaRequestDto dto) {
        Long conceptoSueldoId = dto != null ? dto.getConceptoSueldoId() : null;
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(liq -> {
                if (!"abierto".equals(liq.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                        "Solo se liquida una nómina abierta (las correcciones se hacen con reversa)"));
                }
                Long grupo = liq.getGrupo_nomina_id();
                Mono<Void> validaConcepto = conceptoSueldoId == null ? Mono.empty()
                    : conceptoQueryRepository.existsActivoEnEmpresa(conceptoSueldoId, t.getEmpresaId()).flatMap(ok ->
                        Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                            : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El concepto de sueldo no existe")));

                Mono<LiquidacionNominaResponseDto> proceso = Mono.zip(
                        queryRepo.findVinculacionesActivas(t.getEmpresaId(), grupo).collectList(),
                        queryRepo.findNovedadesPendientes(t.getEmpresaId(), grupo).collectList())
                    .flatMap(tuple -> {
                        List<VinculacionRow> vincs = tuple.getT1();
                        List<NovedadRow> novs = tuple.getT2();
                        LocalDateTime now = LocalDateTime.now();
                        List<LiquidacionNominaDetalleEntity> detalles = new ArrayList<>();
                        List<AportePilaEntity> pilas = new ArrayList<>();

                        for (VinculacionRow v : vincs) {
                            if (conceptoSueldoId != null && v.sueldo() != null && v.sueldo().signum() > 0) {
                                detalles.add(detalleSueldo(liq, v, conceptoSueldoId, now, t.getUsuarioId()));
                            }
                            if (v.sueldo() != null && v.sueldo().signum() > 0) {
                                pilas.add(pila(liq, v, "salud", SALUD_EMP, SALUD_PAT, now));
                                pilas.add(pila(liq, v, "pension", PENSION_EMP, PENSION_PAT, now));
                            }
                        }
                        for (NovedadRow n : novs) {
                            detalles.add(detalleNovedad(liq, n, now, t.getUsuarioId()));
                        }

                        Mono<Void> saveDet = detalles.isEmpty() ? Mono.empty() : detalleRepo.saveAll(detalles).then();
                        Mono<Void> savePila = pilas.isEmpty() ? Mono.empty() : pilaRepo.saveAll(pilas).then();
                        return saveDet.then(savePila)
                            .then(queryRepo.marcarNovedadesAplicadas(t.getEmpresaId(), grupo, liq.getFecha()))
                            .then(Mono.defer(() -> {
                                liq.setEstado("liquidado");
                                liq.setUpdated_at(now);
                                liq.setUsuario_modificacion(t.getUsuarioId());
                                return repo.save(liq);
                            }))
                            .then(findById(id));
                    });
                return validaConcepto.then(proceso).as(tx::transactional);
            }));
    }

    // ==================== Contabilizar (interfaz contable por evento) ====================
    @Override
    public Mono<LiquidacionNominaResponseDto> contabilizar(Long id, ContabilizarNominaRequestDto params) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(liq -> {
                if (!"liquidado".equals(liq.getEstado()) && !"revisado".equals(liq.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                        "Primero liquide la nómina antes de contabilizar"));
                }
                return queryRepo.resumenPorClase(id, t.getEmpresaId()).collectList().flatMap(resumen -> {
                    BigDecimal devengado = sumClase(resumen, "devengado");
                    BigDecimal deduccion = sumClase(resumen, "deduccion");
                    if (devengado.signum() == 0) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "No hay valores que contabilizar"));
                    }
                    List<CreateMovimientoContableDto> movimientos = new ArrayList<>();
                    movimientos.add(mov(params.getCuentaGastoId(), devengado, BigDecimal.ZERO,
                        "Gasto nómina - liquidación #" + id));
                    BigDecimal neto = devengado;
                    if (deduccion.signum() > 0 && params.getCuentaDeduccionesId() != null) {
                        movimientos.add(mov(params.getCuentaDeduccionesId(), BigDecimal.ZERO, deduccion,
                            "Deducciones nómina - liquidación #" + id));
                        neto = devengado.subtract(deduccion);
                    }
                    movimientos.add(mov(params.getCuentaPorPagarId(), BigDecimal.ZERO, neto,
                        "Neto por pagar nómina - liquidación #" + id));

                    AsientoContableSolicitado evento = new AsientoContableSolicitado(
                        t.getEmpresaId(), t.getUsuarioId(), t.getSedeId(),
                        params.getPeriodoContableId(), liq.getFecha(),
                        "Liquidación de nómina #" + id, "nomina", id, movimientos);
                    eventBus.publish(evento);

                    liq.setEstado("contabilizado");
                    liq.setUpdated_at(LocalDateTime.now());
                    liq.setUsuario_modificacion(t.getUsuarioId());
                    return repo.save(liq).then(findById(id));
                });
            }));
    }

    @Override
    public Mono<LiquidacionNominaResponseDto> anular(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(liq -> {
                if ("cerrado".equals(liq.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "No se anula una nómina cerrada"));
                }
                liq.setEstado("anulado");
                liq.setUpdated_at(LocalDateTime.now());
                liq.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(liq).then(findById(id));
            }));
    }

    @Override
    public Mono<List<LiquidacionDetalleDto>> listDetalle(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).then(queryRepo.listDetalle(id, t.getEmpresaId())));
    }

    @Override
    public Mono<List<AportePilaDto>> listPila(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).then(queryRepo.listPila(id, t.getEmpresaId())));
    }

    // ==================== helpers ====================

    private LiquidacionNominaDetalleEntity detalleSueldo(LiquidacionNominaEntity liq, VinculacionRow v,
            Long conceptoSueldoId, LocalDateTime now, Long usuarioId) {
        LiquidacionNominaDetalleEntity d = new LiquidacionNominaDetalleEntity();
        d.setEmpresa_id(liq.getEmpresa_id());
        d.setLiquidacion_nomina_id(liq.getId());
        d.setEmpleado_id(v.empleadoId());
        d.setVinculacion_id(v.vinculacionId());
        d.setConcepto_nomina_id(conceptoSueldoId);
        d.setFecha_liquidacion(now);
        d.setBase(v.sueldo());
        d.setCantidad(BigDecimal.ONE);
        d.setValor(v.sueldo());
        d.setCreated_at(now);
        d.setUsuario_creacion(usuarioId);
        return d;
    }

    private LiquidacionNominaDetalleEntity detalleNovedad(LiquidacionNominaEntity liq, NovedadRow n,
            LocalDateTime now, Long usuarioId) {
        BigDecimal valor = n.cantidadValor() != null ? n.cantidadValor() : BigDecimal.ZERO;
        LiquidacionNominaDetalleEntity d = new LiquidacionNominaDetalleEntity();
        d.setEmpresa_id(liq.getEmpresa_id());
        d.setLiquidacion_nomina_id(liq.getId());
        d.setEmpleado_id(n.empleadoId());
        d.setVinculacion_id(n.vinculacionId());
        d.setConcepto_nomina_id(n.conceptoNominaId());
        d.setFecha_liquidacion(now);
        d.setCantidad(valor);
        d.setValor(valor);
        d.setCreated_at(now);
        d.setUsuario_creacion(usuarioId);
        return d;
    }

    private AportePilaEntity pila(LiquidacionNominaEntity liq, VinculacionRow v, String tipo,
            BigDecimal pctEmpleado, BigDecimal pctPatrono, LocalDateTime now) {
        BigDecimal ibc = v.sueldo();
        AportePilaEntity p = new AportePilaEntity();
        p.setEmpresa_id(liq.getEmpresa_id());
        p.setLiquidacion_nomina_id(liq.getId());
        p.setEmpleado_id(v.empleadoId());
        p.setTipo_aporte(tipo);
        p.setIbc(ibc);
        p.setValor_empleado(ibc.multiply(pctEmpleado).setScale(4, RoundingMode.HALF_UP));
        p.setValor_patrono(ibc.multiply(pctPatrono).setScale(4, RoundingMode.HALF_UP));
        p.setCreated_at(now);
        return p;
    }

    private CreateMovimientoContableDto mov(Long cuentaId, BigDecimal debito, BigDecimal credito, String descripcion) {
        CreateMovimientoContableDto m = new CreateMovimientoContableDto();
        m.setCuentaId(cuentaId);
        m.setDescripcion(descripcion);
        m.setDebito(debito);
        m.setCredito(credito);
        return m;
    }

    private BigDecimal sumClase(List<ResumenClase> resumen, String clase) {
        return resumen.stream()
            .filter(r -> clase.equals(r.clase()))
            .map(r -> r.total() != null ? r.total() : BigDecimal.ZERO)
            .findFirst().orElse(BigDecimal.ZERO);
    }

    private Mono<Void> validarGrupo(Long grupoNominaId, Long empresaId) {
        if (grupoNominaId == null) return Mono.empty();
        return grupoQueryRepository.existsActivoEnEmpresa(grupoNominaId, empresaId).flatMap(ok ->
            Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El grupo de nómina no existe")));
    }

    private Mono<LiquidacionNominaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Liquidación no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Liquidación no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
