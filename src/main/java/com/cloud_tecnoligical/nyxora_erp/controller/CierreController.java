package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.reportes.CierrePeriodoResultDto;
import com.cloud_tecnoligical.nyxora_erp.service.CierreService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cierres")
@Tag(name = "Cierres", description = "Cierre orquestado de periodos contables")
public class CierreController {

    private final CierreService service;

    public CierreController(CierreService service) {
        this.service = service;
    }

    @PostMapping("/periodo/{periodoContableId}")
    @Operation(summary = "Cierre orquestado: valida borradores, recalcula saldos y cierra el periodo")
    public Mono<ResponseEntity<ApiResponse<CierrePeriodoResultDto>>> cerrarPeriodo(@PathVariable Long periodoContableId) {
        return service.cerrarPeriodo(periodoContableId)
            .map(r -> ResponseEntity.ok(new ApiResponse<>(200, "Periodo cerrado", false, r)));
    }
}
