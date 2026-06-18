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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateVinculacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateVinculacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.VinculacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.VinculacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.service.VinculacionService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/vinculaciones")
@Tag(name = "Vinculaciones", description = "Vinculación laboral del empleado")
public class VinculacionController {

    private final VinculacionService service;

    public VinculacionController(VinculacionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear vinculación")
    public Mono<ResponseEntity<ApiResponse<VinculacionResponseDto>>> create(@Valid @RequestBody CreateVinculacionRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Vinculación creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar vinculación")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateVinculacionRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Vinculación actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vinculación (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Vinculación eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar vinculación por id")
    public Mono<ResponseEntity<ApiResponse<VinculacionResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar vinculaciones (paginado; filtro opcional empleadoId)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<VinculacionTableDto>>>> list(
            @RequestBody PageableDto<Void> request,
            @RequestParam(required = false) Long empleadoId) {
        return service.list(request, empleadoId)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
