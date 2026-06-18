package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.AportePilaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ContabilizarNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateLiquidacionNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionDetalleDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidarNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateLiquidacionNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.LiquidacionNominaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/liquidaciones-nomina")
@Tag(name = "Liquidaciones de nómina", description = "Liquidación (detalle + PILA append-only) e interfaz contable")
public class LiquidacionNominaController {

    private final LiquidacionNominaService service;

    public LiquidacionNominaController(LiquidacionNominaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear liquidación (abierta)")
    public Mono<ResponseEntity<ApiResponse<LiquidacionNominaResponseDto>>> create(@Valid @RequestBody CreateLiquidacionNominaRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Liquidación creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar liquidación (solo abierta)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateLiquidacionNominaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Liquidación actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar liquidación (lógico, solo abierta)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Liquidación eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar liquidación por id")
    public Mono<ResponseEntity<ApiResponse<LiquidacionNominaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar liquidaciones (paginado; filtro opcional grupoNominaId)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<LiquidacionNominaTableDto>>>> list(
            @RequestBody PageableDto<Void> request,
            @RequestParam(required = false) Long grupoNominaId) {
        return service.list(request, grupoNominaId)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/liquidar")
    @Operation(summary = "Liquidar: genera el detalle y los aportes PILA (append-only)")
    public Mono<ResponseEntity<ApiResponse<LiquidacionNominaResponseDto>>> liquidar(
            @PathVariable Long id, @RequestBody(required = false) LiquidarNominaRequestDto dto) {
        return service.liquidar(id, dto)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "Nómina liquidada", false, m)));
    }

    @PostMapping("/{id}/contabilizar")
    @Operation(summary = "Contabilizar: publica el asiento contable (interfaz contable por evento)")
    public Mono<ResponseEntity<ApiResponse<LiquidacionNominaResponseDto>>> contabilizar(
            @PathVariable Long id, @Valid @RequestBody ContabilizarNominaRequestDto dto) {
        return service.contabilizar(id, dto)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "Nómina contabilizada", false, m)));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular liquidación")
    public Mono<ResponseEntity<ApiResponse<LiquidacionNominaResponseDto>>> anular(@PathVariable Long id) {
        return service.anular(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "Liquidación anulada", false, m)));
    }

    @GetMapping("/{id}/detalle")
    @Operation(summary = "Detalle de la liquidación (por empleado/concepto)")
    public Mono<ResponseEntity<ApiResponse<List<LiquidacionDetalleDto>>>> detalle(@PathVariable Long id) {
        return service.listDetalle(id)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @GetMapping("/{id}/pila")
    @Operation(summary = "Aportes PILA de la liquidación")
    public Mono<ResponseEntity<ApiResponse<List<AportePilaDto>>>> pila(@PathVariable Long id) {
        return service.listPila(id)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }
}
