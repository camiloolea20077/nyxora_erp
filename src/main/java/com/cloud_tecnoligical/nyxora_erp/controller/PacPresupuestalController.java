package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.PacPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.PacUpsertRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.PacPresupuestalService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pac-presupuestal")
@Tag(name = "PAC presupuestal", description = "Plan Anualizado de Caja por rubro y mes (Presupuesto)")
public class PacPresupuestalController {

    private final PacPresupuestalService service;

    public PacPresupuestalController(PacPresupuestalService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Definir/ajustar el PAC de un rubro para un mes")
    public Mono<ResponseEntity<ApiResponse<PacPresupuestalResponseDto>>> upsert(@Valid @RequestBody PacUpsertRequestDto dto) {
        return service.upsert(dto)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "PAC registrado", false, p)));
    }

    @GetMapping
    @Operation(summary = "Listar el PAC de un rubro por año")
    public Mono<ResponseEntity<ApiResponse<List<PacPresupuestalResponseDto>>>> list(
            @RequestParam Long rubroId, @RequestParam Integer anio) {
        return service.listByRubroAnio(rubroId, anio)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }
}
