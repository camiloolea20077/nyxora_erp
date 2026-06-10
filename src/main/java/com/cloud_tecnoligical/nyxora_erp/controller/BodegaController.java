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
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponsableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateBodegaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateBodegaResponsableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateBodegaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.BodegaResponsableService;
import com.cloud_tecnoligical.nyxora_erp.service.BodegaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bodegas")
@Tag(name = "Bodegas", description = "Bodegas y sus responsables (Inventario)")
public class BodegaController {

    private final BodegaService service;
    private final BodegaResponsableService responsableService;

    public BodegaController(BodegaService service, BodegaResponsableService responsableService) {
        this.service = service;
        this.responsableService = responsableService;
    }

    @PostMapping
    @Operation(summary = "Crear bodega")
    public Mono<ResponseEntity<ApiResponse<BodegaResponseDto>>> create(@Valid @RequestBody CreateBodegaRequestDto dto) {
        return service.create(dto)
            .map(b -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Bodega creada", false, b)));
    }

    @PutMapping
    @Operation(summary = "Actualizar bodega")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateBodegaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Bodega actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar bodega (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Bodega eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar bodega por id")
    public Mono<ResponseEntity<ApiResponse<BodegaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(b -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, b)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar bodegas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<BodegaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    // ---------- Responsables ----------
    @GetMapping("/{bodegaId}/responsables")
    @Operation(summary = "Listar responsables de la bodega")
    public Mono<ResponseEntity<ApiResponse<List<BodegaResponsableResponseDto>>>> listResponsables(@PathVariable Long bodegaId) {
        return responsableService.list(bodegaId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/{bodegaId}/responsables")
    @Operation(summary = "Agregar responsable a la bodega")
    public Mono<ResponseEntity<ApiResponse<BodegaResponsableResponseDto>>> createResponsable(
            @PathVariable Long bodegaId, @Valid @RequestBody CreateBodegaResponsableDto dto) {
        return responsableService.create(bodegaId, dto)
            .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Responsable agregado", false, r)));
    }

    @DeleteMapping("/{bodegaId}/responsables/{id}")
    @Operation(summary = "Eliminar responsable de la bodega")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> deleteResponsable(@PathVariable Long bodegaId, @PathVariable Long id) {
        return responsableService.delete(bodegaId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Responsable eliminado", false, ok)));
    }
}
