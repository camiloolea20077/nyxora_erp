package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.ApropiarRubroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.SaldoPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.SaldoPresupuestalEntity;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.AfectacionPresupuestalQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.RubroPresupuestalR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.SaldoPresupuestalQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.SaldoPresupuestalR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.SaldoPresupuestalService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class SaldoPresupuestalServiceImpl implements SaldoPresupuestalService {

    /** Las afectaciones y la apropiación se consolidan en el saldo anual (mes 0). */
    private static final int MES_ANUAL = 0;

    private final SaldoPresupuestalR2dbcRepository repo;
    private final SaldoPresupuestalQueryRepository queryRepo;
    private final AfectacionPresupuestalQueryRepository afectacionQuery;
    private final RubroPresupuestalR2dbcRepository rubroRepo;

    public SaldoPresupuestalServiceImpl(SaldoPresupuestalR2dbcRepository repo,
                                        SaldoPresupuestalQueryRepository queryRepo,
                                        AfectacionPresupuestalQueryRepository afectacionQuery,
                                        RubroPresupuestalR2dbcRepository rubroRepo) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.afectacionQuery = afectacionQuery;
        this.rubroRepo = rubroRepo;
    }

    @Override
    public Mono<SaldoPresupuestalResponseDto> apropiar(ApropiarRubroRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarRubro(dto.getRubroPresupuestalId(), t.getEmpresaId())
                .then(cargarOCrear(dto.getRubroPresupuestalId(), dto.getAnio(), t.getEmpresaId()))
                .flatMap(s -> {
                    s.setPlan_inicial(nz(dto.getPlanInicial()));
                    s.setAdiciones(nz(dto.getAdiciones()));
                    s.setReducciones(nz(dto.getReducciones()));
                    s.setAplazamientos(nz(dto.getAplazamientos()));
                    s.setCreditos(nz(dto.getCreditos()));
                    s.setContra_creditos(nz(dto.getContraCreditos()));
                    s.setFecha_recalculo(LocalDateTime.now());
                    return repo.save(s);
                })
                .then(findByRubroAnio(dto.getRubroPresupuestalId(), dto.getAnio())));
    }

    @Override
    public Mono<SaldoPresupuestalResponseDto> recalcular(Long rubroId, Integer anio) {
        return TenantContext.get().flatMap(t ->
            validarRubro(rubroId, t.getEmpresaId())
                .then(cargarOCrear(rubroId, anio, t.getEmpresaId()))
                .flatMap(s -> afectacionQuery.sumarPorTipo(rubroId, t.getEmpresaId()).flatMap(sumas -> {
                    s.setDisponibilidad(get(sumas, "disponibilidad"));
                    s.setCompromiso(get(sumas, "compromiso"));
                    s.setObligacion(get(sumas, "obligacion"));
                    s.setPagado(get(sumas, "pago"));
                    s.setReconocimientos(get(sumas, "reconocimiento"));
                    s.setRecaudos(get(sumas, "recaudo"));
                    s.setFecha_recalculo(LocalDateTime.now());
                    return repo.save(s);
                }))
                .then(findByRubroAnio(rubroId, anio)));
    }

    @Override
    public Mono<SaldoPresupuestalResponseDto> findByRubroAnio(Long rubroId, Integer anio) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findByRubroAnioMes(rubroId, anio, MES_ANUAL, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "No hay saldo presupuestal para ese rubro y año"))));
    }

    @Override
    public Mono<List<SaldoPresupuestalResponseDto>> listByRubro(Long rubroId) {
        return TenantContext.get().flatMap(t -> queryRepo.listByRubro(rubroId, t.getEmpresaId()));
    }

    // ==================== helpers ====================

    private Mono<Void> validarRubro(Long rubroId, Long empresaId) {
        return rubroRepo.findById(rubroId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El rubro presupuestal no existe")))
            .flatMap(r -> {
                if (r.getDeleted_at() != null || !r.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El rubro presupuestal no existe"));
                }
                return Mono.<Void>empty();
            });
    }

    /** Carga el saldo (rubro, anio, mes 0) o crea uno nuevo en ceros. */
    private Mono<SaldoPresupuestalEntity> cargarOCrear(Long rubroId, Integer anio, Long empresaId) {
        return queryRepo.findIdByRubroAnioMes(rubroId, anio, MES_ANUAL, empresaId)
            .flatMap(repo::findById)
            .switchIfEmpty(Mono.defer(() -> {
                SaldoPresupuestalEntity s = new SaldoPresupuestalEntity();
                s.setEmpresa_id(empresaId);
                s.setRubro_presupuestal_id(rubroId);
                s.setAnio(anio);
                s.setMes(MES_ANUAL);
                s.setPlan_inicial(BigDecimal.ZERO);
                s.setAdiciones(BigDecimal.ZERO);
                s.setReducciones(BigDecimal.ZERO);
                s.setAplazamientos(BigDecimal.ZERO);
                s.setCreditos(BigDecimal.ZERO);
                s.setContra_creditos(BigDecimal.ZERO);
                s.setDisponibilidad(BigDecimal.ZERO);
                s.setCompromiso(BigDecimal.ZERO);
                s.setObligacion(BigDecimal.ZERO);
                s.setPagado(BigDecimal.ZERO);
                s.setReconocimientos(BigDecimal.ZERO);
                s.setRecaudos(BigDecimal.ZERO);
                s.setFecha_recalculo(LocalDateTime.now());
                return Mono.just(s);
            }));
    }

    private static BigDecimal get(Map<String, BigDecimal> m, String k) {
        return m.getOrDefault(k, BigDecimal.ZERO);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
