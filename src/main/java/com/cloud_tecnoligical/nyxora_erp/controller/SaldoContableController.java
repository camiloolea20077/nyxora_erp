package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.RecalcularSaldosRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.SaldoContableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.SaldoContableService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/saldos")
@Tag(name = "Saldos contables", description = "Proyección de saldos recalculable desde movimientos (Contabilidad)")
public class SaldoContableController {

    private final SaldoContableService service;

    public SaldoContableController(SaldoContableService service) {
        this.service = service;
    }

    @PostMapping("/recalcular")
    @Operation(summary = "Recalcular saldos de un periodo (borra y reconstruye desde movimientos)")
    public Mono<ResponseEntity<ApiResponse<Long>>> recalcular(@Valid @RequestBody RecalcularSaldosRequestDto dto) {
        return service.recalcular(dto.getPeriodoContableId())
            .map(filas -> ResponseEntity.ok(new ApiResponse<>(200, "Saldos recalculados", false, filas)));
    }

    @GetMapping
    @Operation(summary = "Consultar saldos de un periodo (opcional por cuenta)")
    public Mono<ResponseEntity<ApiResponse<List<SaldoContableResponseDto>>>> consultar(
            @RequestParam Long periodoContableId, @RequestParam(required = false) Long cuentaId) {
        return service.consultar(periodoContableId, cuentaId)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }
}
