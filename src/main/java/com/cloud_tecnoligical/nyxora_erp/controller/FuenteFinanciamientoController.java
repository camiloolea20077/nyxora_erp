package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateFuenteFinanciamientoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.FuenteFinanciamientoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.FuenteFinanciamientoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.UpdateFuenteFinanciamientoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.FuenteFinanciamientoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/fuentes-financiamiento")
@Tag(name = "Fuentes de financiamiento", description = "Fuentes de financiamiento presupuestal (Presupuesto)")
public class FuenteFinanciamientoController {

    private final FuenteFinanciamientoService service;

    public FuenteFinanciamientoController(FuenteFinanciamientoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear fuente de financiamiento")
    public Mono<ResponseEntity<ApiResponse<FuenteFinanciamientoResponseDto>>> create(@Valid @RequestBody CreateFuenteFinanciamientoRequestDto dto) {
        return service.create(dto)
            .map(f -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Fuente creada", false, f)));
    }

    @PutMapping
    @Operation(summary = "Actualizar fuente de financiamiento")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateFuenteFinanciamientoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Fuente actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar fuente de financiamiento (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Fuente eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar fuente por id")
    public Mono<ResponseEntity<ApiResponse<FuenteFinanciamientoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(f -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, f)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar fuentes (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<FuenteFinanciamientoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
