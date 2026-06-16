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

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreatePolizaSeguroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.PolizaSeguroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.PolizaSeguroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.UpdatePolizaSeguroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.PolizaSeguroService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/polizas-seguro")
@Tag(name = "Pólizas de seguro", description = "Pólizas de seguro (Activos Fijos / Contratación)")
public class PolizaSeguroController {

    private final PolizaSeguroService service;

    public PolizaSeguroController(PolizaSeguroService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear póliza de seguro")
    public Mono<ResponseEntity<ApiResponse<PolizaSeguroResponseDto>>> create(@Valid @RequestBody CreatePolizaSeguroRequestDto dto) {
        return service.create(dto)
            .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Póliza creada", false, p)));
    }

    @PutMapping
    @Operation(summary = "Actualizar póliza de seguro")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdatePolizaSeguroRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Póliza actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar póliza de seguro (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Póliza eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar póliza por id")
    public Mono<ResponseEntity<ApiResponse<PolizaSeguroResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar pólizas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<PolizaSeguroTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
