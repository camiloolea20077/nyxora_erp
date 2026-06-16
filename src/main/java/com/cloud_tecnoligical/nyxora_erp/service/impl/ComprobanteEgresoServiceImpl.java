package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateMovimientoContableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ComprobanteEgresoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ComprobanteEgresoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateComprobanteEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.GirarEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.UpdateComprobanteEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ComprobanteEgresoEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ObligacionPagoEntity;
import com.cloud_tecnoligical.nyxora_erp.event.AsientoContableSolicitado;
import com.cloud_tecnoligical.nyxora_erp.event.DomainEventBus;
import com.cloud_tecnoligical.nyxora_erp.mapper.tesoreria.ComprobanteEgresoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar.ObligacionPagoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tesoreria.ComprobanteEgresoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tesoreria.ComprobanteEgresoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.ComprobanteEgresoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ComprobanteEgresoServiceImpl implements ComprobanteEgresoService {

    private static final String ORIGEN_CXP = "cuentas_por_pagar";

    private final ComprobanteEgresoR2dbcRepository repo;
    private final ComprobanteEgresoQueryRepository queryRepo;
    private final ObligacionPagoR2dbcRepository obligacionRepo;
    private final ComprobanteEgresoMapper mapper;
    private final DomainEventBus eventBus;
    private final TransactionalOperator tx;

    public ComprobanteEgresoServiceImpl(ComprobanteEgresoR2dbcRepository repo,
                                        ComprobanteEgresoQueryRepository queryRepo,
                                        ObligacionPagoR2dbcRepository obligacionRepo, ComprobanteEgresoMapper mapper,
                                        DomainEventBus eventBus, ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.obligacionRepo = obligacionRepo;
        this.mapper = mapper;
        this.eventBus = eventBus;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<ComprobanteEgresoResponseDto> create(CreateComprobanteEgresoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                ComprobanteEgresoEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setEstado("borrador");
                if (dto.getObligacionPagoId() != null) {
                    e.setOrigen_modulo(ORIGEN_CXP);
                    e.setOrigen_id(dto.getObligacionPagoId());
                }
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateComprobanteEgresoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e -> {
                if (!"borrador".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se edita un egreso en borrador"));
                }
                return validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                    e.setCuenta_bancaria_id(dto.getCuentaBancariaId());
                    e.setBeneficiario_id(dto.getBeneficiarioId());
                    e.setTipo_documento_id(dto.getTipoDocumentoId());
                    e.setForma_pago_id(dto.getFormaPagoId());
                    e.setNumero(dto.getNumero());
                    e.setFecha(dto.getFecha());
                    e.setValor(dto.getValor());
                    e.setNumero_cheque(dto.getNumeroCheque());
                    e.setDescripcion(dto.getDescripcion());
                    if (dto.getObligacionPagoId() != null) {
                        e.setOrigen_modulo(ORIGEN_CXP);
                        e.setOrigen_id(dto.getObligacionPagoId());
                    } else {
                        e.setOrigen_modulo(null);
                        e.setOrigen_id(null);
                    }
                    e.setUsuario_modificacion(t.getUsuarioId());
                    e.setUpdated_at(LocalDateTime.now());
                    return repo.save(e).thenReturn(true);
                }));
            }));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                if (!"borrador".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se elimina un egreso en borrador"));
                }
                e.setDeleted_at(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<ComprobanteEgresoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Egreso no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<ComprobanteEgresoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<ComprobanteEgresoResponseDto> girar(Long id, GirarEgresoRequestDto params) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                if (!"borrador".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se gira un egreso en borrador"));
                }
                e.setEstado("girado");
                e.setUsuario_modificacion(t.getUsuarioId());
                e.setUpdated_at(LocalDateTime.now());
                Mono<Void> flujo = aplicarAObligacion(e, t).then(repo.save(e).then());
                return flujo.as(tx::transactional)
                    .then(Mono.fromRunnable(() -> publicarAsiento(params, e, t)))
                    .then(findById(id));
            }));
    }

    @Override
    public Mono<Boolean> anular(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                if (!"borrador".equals(e.getEstado()) && !"girado".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se anula un egreso en borrador o girado"));
                }
                boolean reversar = "girado".equals(e.getEstado());
                e.setEstado("anulado");
                e.setUsuario_modificacion(t.getUsuarioId());
                e.setUpdated_at(LocalDateTime.now());
                Mono<Void> flujo = (reversar ? reversarObligacion(e) : Mono.<Void>empty()).then(repo.save(e).then());
                return flujo.as(tx::transactional).thenReturn(true);
            }));
    }

    // ==================== helpers ====================

    /** Si el egreso aplica a una obligación, reduce su saldo (girar). */
    private Mono<Void> aplicarAObligacion(ComprobanteEgresoEntity e, TenantInfo t) {
        if (!ORIGEN_CXP.equals(e.getOrigen_modulo()) || e.getOrigen_id() == null) {
            return Mono.empty();
        }
        return cargarObligacion(e.getOrigen_id(), t.getEmpresaId()).flatMap(o -> {
            if (nz(e.getValor()).compareTo(nz(o.getSaldo())) > 0) {
                return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El valor del egreso supera el saldo de la obligación"));
            }
            BigDecimal nuevoSaldo = nz(o.getSaldo()).subtract(nz(e.getValor()));
            o.setSaldo(nuevoSaldo);
            o.setEstado(nuevoSaldo.signum() == 0 ? "pagada" : "parcial");
            o.setUsuario_modificacion(t.getUsuarioId());
            o.setUpdated_at(LocalDateTime.now());
            return obligacionRepo.save(o).then();
        });
    }

    /** Reversa la aplicación al anular: devuelve el saldo a la obligación. */
    private Mono<Void> reversarObligacion(ComprobanteEgresoEntity e) {
        if (!ORIGEN_CXP.equals(e.getOrigen_modulo()) || e.getOrigen_id() == null) {
            return Mono.empty();
        }
        return obligacionRepo.findById(e.getOrigen_id()).flatMap(o -> {
            BigDecimal nuevoSaldo = nz(o.getSaldo()).add(nz(e.getValor()));
            o.setSaldo(nuevoSaldo);
            o.setEstado(nuevoSaldo.compareTo(nz(o.getValor_total())) >= 0 ? "pendiente" : "parcial");
            o.setUpdated_at(LocalDateTime.now());
            return obligacionRepo.save(o).then();
        });
    }

    /** Publica el asiento (débito CxP / crédito banco) si se proveen cuentas + periodo. */
    private void publicarAsiento(GirarEgresoRequestDto params, ComprobanteEgresoEntity e, TenantInfo t) {
        if (params == null || params.getCuentaBancoId() == null
                || params.getCuentaCxpId() == null || params.getPeriodoContableId() == null) {
            return;
        }
        BigDecimal valor = nz(e.getValor());
        if (valor.signum() == 0) {
            return;
        }
        CreateMovimientoContableDto debito = new CreateMovimientoContableDto();
        debito.setCuentaId(params.getCuentaCxpId());
        debito.setTerceroId(e.getBeneficiario_id());
        debito.setDescripcion("CxP - egreso #" + e.getId());
        debito.setDebito(valor);
        debito.setCredito(BigDecimal.ZERO);

        CreateMovimientoContableDto credito = new CreateMovimientoContableDto();
        credito.setCuentaId(params.getCuentaBancoId());
        credito.setDescripcion("Banco - egreso #" + e.getId());
        credito.setDebito(BigDecimal.ZERO);
        credito.setCredito(valor);

        AsientoContableSolicitado evento = new AsientoContableSolicitado(
            t.getEmpresaId(), t.getUsuarioId(), t.getSedeId(),
            params.getPeriodoContableId(), e.getFecha(),
            "Comprobante de egreso #" + e.getId(), "tesoreria", e.getId(),
            List.of(debito, credito));
        eventBus.publish(evento);
    }

    private Mono<Void> validarReferencias(CreateComprobanteEgresoRequestDto dto, Long empresaId) {
        Mono<Void> vBenef = queryRepo.terceroExisteEnEmpresa(dto.getBeneficiarioId(), empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El beneficiario no existe")));
        Mono<Void> vCuenta = dto.getCuentaBancariaId() == null ? Mono.empty()
            : queryRepo.cuentaBancariaExisteEnEmpresa(dto.getCuentaBancariaId(), empresaId)
                .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La cuenta bancaria no existe")));
        return vBenef.then(vCuenta);
    }

    private Mono<ComprobanteEgresoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Egreso no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Egreso no encontrado"));
                }
                return Mono.just(e);
            });
    }

    private Mono<ObligacionPagoEntity> cargarObligacion(Long id, Long empresaId) {
        return obligacionRepo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La obligación de pago no existe")))
            .flatMap(o -> {
                if (o.getDeleted_at() != null || !o.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La obligación de pago no existe"));
                }
                return Mono.just(o);
            });
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
