package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.reportes.BalanceGeneralDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.BalanceLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.CarteraTerceroDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.EjecucionRubroDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.EstadoResultadosDto;
import com.cloud_tecnoligical.nyxora_erp.repository.reportes.ReporteQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ReporteService;

import reactor.core.publisher.Mono;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteQueryRepository queryRepo;

    public ReporteServiceImpl(ReporteQueryRepository queryRepo) {
        this.queryRepo = queryRepo;
    }

    @Override
    public Mono<BalanceGeneralDto> balanceGeneral(Long periodoContableId) {
        return TenantContext.get().flatMap(t ->
            queryRepo.lineasBalance(periodoContableId, t.getEmpresaId()).map(lineas -> {
                BalanceGeneralDto dto = new BalanceGeneralDto();
                dto.setPeriodoContableId(periodoContableId);
                List<BalanceLineaDto> activos = lineas.stream().filter(l -> "1".equals(l.getClase())).toList();
                List<BalanceLineaDto> pasivos = lineas.stream().filter(l -> "2".equals(l.getClase())).toList();
                List<BalanceLineaDto> patrimonio = lineas.stream().filter(l -> "3".equals(l.getClase())).toList();
                // pasivo/patrimonio son de naturaleza crédito: saldo = crédito - débito
                pasivos.forEach(this::invertirSaldo);
                patrimonio.forEach(this::invertirSaldo);
                dto.setActivos(activos);
                dto.setPasivos(pasivos);
                dto.setPatrimonio(patrimonio);
                BigDecimal totalActivo = sumSaldo(activos);
                BigDecimal totalPasivo = sumSaldo(pasivos);
                BigDecimal totalPatrimonio = sumSaldo(patrimonio);
                dto.setTotalActivo(totalActivo);
                dto.setTotalPasivo(totalPasivo);
                dto.setTotalPatrimonio(totalPatrimonio);
                BigDecimal totalPasPat = totalPasivo.add(totalPatrimonio);
                dto.setTotalPasivoPatrimonio(totalPasPat);
                dto.setDescuadre(totalActivo.subtract(totalPasPat));
                return dto;
            }));
    }

    @Override
    public Mono<EstadoResultadosDto> estadoResultados(Long periodoContableId) {
        return TenantContext.get().flatMap(t ->
            queryRepo.lineasResultados(periodoContableId, t.getEmpresaId()).map(lineas -> {
                EstadoResultadosDto dto = new EstadoResultadosDto();
                dto.setPeriodoContableId(periodoContableId);
                List<BalanceLineaDto> ingresos = lineas.stream().filter(l -> "4".equals(l.getClase())).toList();
                List<BalanceLineaDto> costosGastos = lineas.stream()
                    .filter(l -> "5".equals(l.getClase()) || "6".equals(l.getClase()) || "7".equals(l.getClase()))
                    .toList();
                // ingresos son de naturaleza crédito: saldo = crédito - débito
                ingresos.forEach(this::invertirSaldo);
                dto.setIngresos(ingresos);
                dto.setCostosGastos(costosGastos);
                BigDecimal totalIngresos = sumSaldo(ingresos);
                BigDecimal totalCostosGastos = sumSaldo(costosGastos);
                dto.setTotalIngresos(totalIngresos);
                dto.setTotalCostosGastos(totalCostosGastos);
                dto.setUtilidad(totalIngresos.subtract(totalCostosGastos));
                return dto;
            }));
    }

    @Override
    public Mono<List<CarteraTerceroDto>> cartera() {
        return TenantContext.get().flatMap(t -> queryRepo.cartera(t.getEmpresaId()));
    }

    @Override
    public Mono<List<EjecucionRubroDto>> ejecucionPresupuestal(Long vigenciaId) {
        return TenantContext.get().flatMap(t ->
            queryRepo.ejecucionPresupuestal(t.getEmpresaId(), vigenciaId).map(lista -> {
                lista.forEach(r -> r.setDisponible(nz(r.getApropiacion()).subtract(nz(r.getComprometido()))));
                return lista;
            }));
    }

    // ---------- helpers ----------
    private void invertirSaldo(BalanceLineaDto l) {
        l.setSaldo(nz(l.getCredito()).subtract(nz(l.getDebito())));
    }

    private BigDecimal sumSaldo(List<BalanceLineaDto> lineas) {
        return lineas.stream().map(l -> nz(l.getSaldo())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
