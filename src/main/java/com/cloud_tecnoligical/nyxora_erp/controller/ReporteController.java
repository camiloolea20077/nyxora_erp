package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.reportes.BalanceGeneralDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.CarteraTerceroDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.EjecucionRubroDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.EstadoResultadosDto;
import com.cloud_tecnoligical.nyxora_erp.service.ReporteService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Estados financieros básicos, cartera y ejecución presupuestal")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping("/balance-general")
    @Operation(summary = "Balance general del periodo (activo = pasivo + patrimonio)")
    public Mono<ResponseEntity<ApiResponse<BalanceGeneralDto>>> balanceGeneral(@RequestParam Long periodoContableId) {
        return service.balanceGeneral(periodoContableId)
            .map(b -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, b)));
    }

    @GetMapping("/estado-resultados")
    @Operation(summary = "Estado de resultados del periodo (ingresos - costos/gastos)")
    public Mono<ResponseEntity<ApiResponse<EstadoResultadosDto>>> estadoResultados(@RequestParam Long periodoContableId) {
        return service.estadoResultados(periodoContableId)
            .map(e -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, e)));
    }

    @GetMapping("/cartera")
    @Operation(summary = "Cartera (CxC) por cliente, con saldo vencido")
    public Mono<ResponseEntity<ApiResponse<List<CarteraTerceroDto>>>> cartera() {
        return service.cartera()
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @GetMapping("/ejecucion-presupuestal")
    @Operation(summary = "Ejecución presupuestal por rubro de una vigencia")
    public Mono<ResponseEntity<ApiResponse<List<EjecucionRubroDto>>>> ejecucionPresupuestal(@RequestParam Long vigenciaId) {
        return service.ejecucionPresupuestal(vigenciaId)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }
}
