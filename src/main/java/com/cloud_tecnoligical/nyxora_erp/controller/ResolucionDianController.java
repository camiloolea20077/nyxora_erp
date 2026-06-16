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
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.CreateResolucionDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.ResolucionDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.ResolucionDianTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.UpdateResolucionDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ResolucionDianService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/resoluciones-dian")
@Tag(name = "Resoluciones DIAN", description = "Resoluciones de facturación DIAN (Facturación)")
public class ResolucionDianController {

    private final ResolucionDianService service;

    public ResolucionDianController(ResolucionDianService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear resolución DIAN")
    public Mono<ResponseEntity<ApiResponse<ResolucionDianResponseDto>>> create(@Valid @RequestBody CreateResolucionDianRequestDto dto) {
        return service.create(dto)
            .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Resolución creada", false, r)));
    }

    @PutMapping
    @Operation(summary = "Actualizar resolución DIAN")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateResolucionDianRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Resolución actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar resolución DIAN (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Resolución eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar resolución DIAN por id")
    public Mono<ResponseEntity<ApiResponse<ResolucionDianResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(r -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, r)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar resoluciones DIAN (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ResolucionDianTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
