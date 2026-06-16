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
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.CreateFacturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.EmitirFacturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.RegistrarFacturaDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.UpdateFacturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.FacturaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/facturas")
@Tag(name = "Facturas", description = "Facturación de venta → numeración DIAN + salida de inventario + cartera/contabilidad")
public class FacturaController {

    private final FacturaService service;

    public FacturaController(FacturaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear factura (con líneas, estado borrador)")
    public Mono<ResponseEntity<ApiResponse<FacturaResponseDto>>> create(@Valid @RequestBody CreateFacturaRequestDto dto) {
        return service.create(dto)
            .map(f -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Factura creada", false, f)));
    }

    @PutMapping
    @Operation(summary = "Actualizar factura (solo borrador; reemplaza líneas)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateFacturaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Factura actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar factura (lógico, solo borrador)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Factura eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar factura por id (con líneas)")
    public Mono<ResponseEntity<ApiResponse<FacturaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(f -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, f)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar facturas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<FacturaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/emitir")
    @Operation(summary = "Emitir factura → numera (DIAN), descuenta inventario y (opcional) genera asiento + cartera")
    public Mono<ResponseEntity<ApiResponse<FacturaResponseDto>>> emitir(
            @PathVariable Long id, @RequestBody(required = false) EmitirFacturaRequestDto params) {
        return service.emitir(id, params != null ? params : new EmitirFacturaRequestDto())
            .map(f -> ResponseEntity.ok(new ApiResponse<>(200, "Factura emitida", false, f)));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular factura (borrador/emitida → anulada; reversa inventario si estaba emitida)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> anular(@PathVariable Long id) {
        return service.anular(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Factura anulada", false, ok)));
    }

    @PostMapping("/{id}/dian")
    @Operation(summary = "Registrar/actualizar metadata de factura electrónica (CUFE + acuse DIAN)")
    public Mono<ResponseEntity<ApiResponse<FacturaDianResponseDto>>> registrarDian(
            @PathVariable Long id, @RequestBody RegistrarFacturaDianRequestDto dto) {
        return service.registrarDian(id, dto)
            .map(d -> ResponseEntity.ok(new ApiResponse<>(200, "Factura electrónica registrada", false, d)));
    }
}
