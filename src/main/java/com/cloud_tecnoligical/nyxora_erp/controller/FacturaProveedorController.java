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
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.CreateFacturaProveedorRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.RegistrarEventoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.UpdateFacturaProveedorRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.FacturaProveedorService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/facturas-proveedor")
@Tag(name = "Facturas de proveedor", description = "Recepción de FE de proveedor (DIAN/RADIAN) (Cuentas por pagar)")
public class FacturaProveedorController {

    private final FacturaProveedorService service;

    public FacturaProveedorController(FacturaProveedorService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar factura de proveedor")
    public Mono<ResponseEntity<ApiResponse<FacturaProveedorResponseDto>>> create(@Valid @RequestBody CreateFacturaProveedorRequestDto dto) {
        return service.create(dto)
            .map(f -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Factura de proveedor registrada", false, f)));
    }

    @PutMapping
    @Operation(summary = "Actualizar factura de proveedor (solo 'recibida')")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateFacturaProveedorRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Factura de proveedor actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar factura de proveedor (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Factura de proveedor eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar factura de proveedor por id (con eventos)")
    public Mono<ResponseEntity<ApiResponse<FacturaProveedorResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(f -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, f)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar facturas de proveedor (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<FacturaProveedorTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/eventos")
    @Operation(summary = "Registrar evento RADIAN sobre la factura de proveedor")
    public Mono<ResponseEntity<ApiResponse<FacturaProveedorResponseDto>>> registrarEvento(
            @PathVariable Long id, @RequestBody RegistrarEventoRequestDto dto) {
        return service.registrarEvento(id, dto)
            .map(f -> ResponseEntity.ok(new ApiResponse<>(200, "Evento registrado", false, f)));
    }
}
