package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreatePeriodoContableRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.PeriodoContableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.PeriodoContableTableDto;
import com.cloud_tecnoligical.nyxora_erp.service.PeriodoContableService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/periodos-contables")
@Tag(name = "Periodos contables", description = "Periodos contables y su cierre/apertura (Contabilidad)")
public class PeriodoContableController {

    private final PeriodoContableService service;

    public PeriodoContableController(PeriodoContableService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear periodo contable (abierto)")
    public Mono<ResponseEntity<ApiResponse<PeriodoContableResponseDto>>> create(@Valid @RequestBody CreatePeriodoContableRequestDto dto) {
        return service.create(dto)
            .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Periodo creado", false, p)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar periodo por id")
    public Mono<ResponseEntity<ApiResponse<PeriodoContableResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar periodos contables (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<PeriodoContableTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar periodo contable")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> cerrar(@PathVariable Long id) {
        return service.cerrar(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Periodo cerrado", false, ok)));
    }

    @PostMapping("/{id}/reabrir")
    @Operation(summary = "Reabrir periodo contable")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> reabrir(@PathVariable Long id) {
        return service.reabrir(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Periodo reabierto", false, ok)));
    }
}
