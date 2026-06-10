package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.ComprobanteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.ComprobanteTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateComprobanteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ComprobanteService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/comprobantes")
@Tag(name = "Comprobantes", description = "Asientos contables (partida doble, append-only) (Contabilidad)")
public class ComprobanteController {

    private final ComprobanteService service;

    public ComprobanteController(ComprobanteService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear comprobante con sus movimientos (estado borrador, balanceado)")
    public Mono<ResponseEntity<ApiResponse<ComprobanteResponseDto>>> create(@Valid @RequestBody CreateComprobanteRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Comprobante creado", false, c)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar comprobante por id (con movimientos)")
    public Mono<ResponseEntity<ApiResponse<ComprobanteResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar comprobantes (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ComprobanteTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar comprobante (exige periodo abierto y balance)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> confirmar(@PathVariable Long id) {
        return service.confirmar(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Comprobante confirmado", false, ok)));
    }

    @PostMapping("/{id}/reversar")
    @Operation(summary = "Reversar comprobante confirmado (genera comprobante inverso)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> reversar(@PathVariable Long id) {
        return service.reversar(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Comprobante reversado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar comprobante (lógico, solo en borrador)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Comprobante eliminado", false, ok)));
    }
}
