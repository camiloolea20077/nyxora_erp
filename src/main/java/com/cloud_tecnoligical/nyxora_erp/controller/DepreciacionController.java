package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreateDepreciacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.DepreciacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.DepreciacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.DepreciacionService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/depreciaciones")
@Tag(name = "Depreciaciones", description = "Depreciación de activos fijos (append-only)")
public class DepreciacionController {

    private final DepreciacionService service;

    public DepreciacionController(DepreciacionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar depreciación (append-only)")
    public Mono<ResponseEntity<ApiResponse<DepreciacionResponseDto>>> registrar(@Valid @RequestBody CreateDepreciacionRequestDto dto) {
        return service.registrar(dto)
            .map(d -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Depreciación registrada", false, d)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar depreciación por id")
    public Mono<ResponseEntity<ApiResponse<DepreciacionResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(d -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, d)));
    }

    @PostMapping("/activo/{activoFijoId}/list")
    @Operation(summary = "Listar depreciaciones de un activo fijo (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<DepreciacionTableDto>>>> listByActivo(
            @PathVariable Long activoFijoId, @RequestBody PageableDto<Void> request) {
        return service.listByActivo(activoFijoId, request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
