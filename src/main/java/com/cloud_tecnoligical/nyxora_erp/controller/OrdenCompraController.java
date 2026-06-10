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
import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateOrdenCompraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.UpdateOrdenCompraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.OrdenCompraService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ordenes-compra")
@Tag(name = "Órdenes de compra", description = "Órdenes de compra y su ciclo (Compras)")
public class OrdenCompraController {

    private final OrdenCompraService service;

    public OrdenCompraController(OrdenCompraService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear orden de compra (con líneas, estado borrador)")
    public Mono<ResponseEntity<ApiResponse<OrdenCompraResponseDto>>> create(@Valid @RequestBody CreateOrdenCompraRequestDto dto) {
        return service.create(dto)
            .map(o -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Orden creada", false, o)));
    }

    @PutMapping
    @Operation(summary = "Actualizar orden (solo borrador; reemplaza líneas)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateOrdenCompraRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Orden actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar orden (lógico, solo borrador)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Orden eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar orden por id (con líneas)")
    public Mono<ResponseEntity<ApiResponse<OrdenCompraResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(o -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, o)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar órdenes de compra (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<OrdenCompraTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/aprobar")
    @Operation(summary = "Aprobar orden (borrador → aprobada)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> aprobar(@PathVariable Long id) {
        return service.aprobar(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Orden aprobada", false, ok)));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular orden (borrador/aprobada → anulada)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> anular(@PathVariable Long id) {
        return service.anular(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Orden anulada", false, ok)));
    }
}
