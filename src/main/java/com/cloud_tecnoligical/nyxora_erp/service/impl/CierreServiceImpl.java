package com.cloud_tecnoligical.nyxora_erp.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.reportes.CierrePeriodoResultDto;
import com.cloud_tecnoligical.nyxora_erp.repository.reportes.ReporteQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CierreService;
import com.cloud_tecnoligical.nyxora_erp.service.PeriodoContableService;
import com.cloud_tecnoligical.nyxora_erp.service.SaldoContableService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class CierreServiceImpl implements CierreService {

    private final PeriodoContableService periodoService;
    private final SaldoContableService saldoService;
    private final ReporteQueryRepository reporteQuery;

    public CierreServiceImpl(PeriodoContableService periodoService, SaldoContableService saldoService,
                             ReporteQueryRepository reporteQuery) {
        this.periodoService = periodoService;
        this.saldoService = saldoService;
        this.reporteQuery = reporteQuery;
    }

    @Override
    public Mono<CierrePeriodoResultDto> cerrarPeriodo(Long periodoContableId) {
        return TenantContext.get().flatMap(t ->
            periodoService.findById(periodoContableId).flatMap(periodo -> {
                if (!"abierto".equals(periodo.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El periodo ya está cerrado"));
                }
                return reporteQuery.contarComprobantesBorrador(periodoContableId, t.getEmpresaId()).flatMap(borradores -> {
                    if (borradores > 0) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                            "Hay " + borradores + " comprobante(s) en borrador; confírmalos o reversalos antes de cerrar"));
                    }
                    return saldoService.recalcular(periodoContableId)
                        .flatMap(saldos -> periodoService.cerrar(periodoContableId).thenReturn(saldos))
                        .map(saldos -> {
                            CierrePeriodoResultDto r = new CierrePeriodoResultDto();
                            r.setPeriodoContableId(periodoContableId);
                            r.setAnio(periodo.getAnio());
                            r.setMes(periodo.getMes());
                            r.setEstado("cerrado");
                            r.setSaldosRecalculados(saldos);
                            r.setMensaje("Periodo cerrado: " + saldos + " saldos recalculados");
                            return r;
                        });
                });
            }));
    }
}
