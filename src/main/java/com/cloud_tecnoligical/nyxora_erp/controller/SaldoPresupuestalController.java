package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.ApropiarRubroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.SaldoPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.SaldoPresupuestalService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/saldos-presupuestales")
@Tag(name = "Saldos presupuestales", description = "Apropiación y ejecución por rubro (Presupuesto)")
public class SaldoPresupuestalController {

    private final SaldoPresupuestalService service;

    public SaldoPresupuestalController(SaldoPresupuestalService service) {
        this.service = service;
    }

    @PostMapping("/apropiar")
    @Operation(summary = "Definir/ajustar la apropiación de un rubro para un año")
    public Mono<ResponseEntity<ApiResponse<SaldoPresupuestalResponseDto>>> apropiar(@Valid @RequestBody ApropiarRubroRequestDto dto) {
        return service.apropiar(dto)
            .map(s -> ResponseEntity.ok(new ApiResponse<>(200, "Apropiación registrada", false, s)));
    }

    @PostMapping("/recalcular")
    @Operation(summary = "Recalcular la ejecución del saldo desde las afectaciones")
    public Mono<ResponseEntity<ApiResponse<SaldoPresupuestalResponseDto>>> recalcular(
            @RequestParam Long rubroId, @RequestParam Integer anio) {
        return service.recalcular(rubroId, anio)
            .map(s -> ResponseEntity.ok(new ApiResponse<>(200, "Saldo recalculado", false, s)));
    }

    @GetMapping
    @Operation(summary = "Consultar saldo de un rubro por año")
    public Mono<ResponseEntity<ApiResponse<SaldoPresupuestalResponseDto>>> findByRubroAnio(
            @RequestParam Long rubroId, @RequestParam Integer anio) {
        return service.findByRubroAnio(rubroId, anio)
            .map(s -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, s)));
    }

    @GetMapping("/rubro/{rubroId}")
    @Operation(summary = "Listar saldos de un rubro")
    public Mono<ResponseEntity<ApiResponse<List<SaldoPresupuestalResponseDto>>>> listByRubro(@PathVariable Long rubroId) {
        return service.listByRubro(rubroId)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }
}
