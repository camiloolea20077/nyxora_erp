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
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateUbicacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UbicacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UbicacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateUbicacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.UbicacionService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ubicaciones")
@Tag(name = "Ubicaciones", description = "Ubicaciones jerárquicas dentro de bodega (Inventario)")
public class UbicacionController {

    private final UbicacionService service;

    public UbicacionController(UbicacionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear ubicación")
    public Mono<ResponseEntity<ApiResponse<UbicacionResponseDto>>> create(@Valid @RequestBody CreateUbicacionRequestDto dto) {
        return service.create(dto)
            .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Ubicación creada", false, u)));
    }

    @PutMapping
    @Operation(summary = "Actualizar ubicación")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateUbicacionRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Ubicación actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ubicación (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Ubicación eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar ubicación por id")
    public Mono<ResponseEntity<ApiResponse<UbicacionResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(u -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, u)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar ubicaciones (paginado, filtro opcional por bodega)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<UbicacionTableDto>>>> list(
            @RequestBody PageableDto<Void> request, @RequestParam(required = false) Long bodegaId) {
        return service.list(request, bodegaId)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
