package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.RecalcularSaldoInventarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.SaldoInventarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.SaldoInventarioService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/saldos-inventario")
@Tag(name = "Saldos de inventario", description = "Existencias por bodega (proyección recalculable) (Inventario)")
public class SaldoInventarioController {

    private final SaldoInventarioService service;

    public SaldoInventarioController(SaldoInventarioService service) {
        this.service = service;
    }

    @PostMapping("/recalcular")
    @Operation(summary = "Recalcular saldos de una bodega desde los movimientos")
    public Mono<ResponseEntity<ApiResponse<Long>>> recalcular(@Valid @RequestBody RecalcularSaldoInventarioRequestDto dto) {
        return service.recalcular(dto.getBodegaId())
            .map(filas -> ResponseEntity.ok(new ApiResponse<>(200, "Saldos recalculados", false, filas)));
    }

    @GetMapping
    @Operation(summary = "Consultar existencias de una bodega (opcional por producto)")
    public Mono<ResponseEntity<ApiResponse<List<SaldoInventarioResponseDto>>>> consultar(
            @RequestParam Long bodegaId, @RequestParam(required = false) Long productoId) {
        return service.consultar(bodegaId, productoId)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }
}
