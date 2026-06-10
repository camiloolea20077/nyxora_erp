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
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateDependenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.DependenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.DependenciaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateDependenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.DependenciaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/dependencias")
@Tag(name = "Dependencias", description = "Dependencias jerárquicas bajo centro de costo (Común)")
public class DependenciaController {

    private final DependenciaService service;

    public DependenciaController(DependenciaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear dependencia")
    public Mono<ResponseEntity<ApiResponse<DependenciaResponseDto>>> create(@Valid @RequestBody CreateDependenciaRequestDto dto) {
        return service.create(dto)
            .map(d -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Dependencia creada", false, d)));
    }

    @PutMapping
    @Operation(summary = "Actualizar dependencia")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateDependenciaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Dependencia actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar dependencia (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Dependencia eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar dependencia por id")
    public Mono<ResponseEntity<ApiResponse<DependenciaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(d -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, d)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar dependencias (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<DependenciaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
