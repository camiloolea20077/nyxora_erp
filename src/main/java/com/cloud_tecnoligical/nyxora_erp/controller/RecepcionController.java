package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.ConfirmarRecepcionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateRecepcionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionTableDto;
import com.cloud_tecnoligical.nyxora_erp.service.RecepcionService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/recepciones")
@Tag(name = "Recepciones", description = "Recepción de compras → inventario + asiento contable (Compras)")
public class RecepcionController {

    private final RecepcionService service;

    public RecepcionController(RecepcionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear recepción (con líneas, estado borrador)")
    public Mono<ResponseEntity<ApiResponse<RecepcionResponseDto>>> create(@Valid @RequestBody CreateRecepcionRequestDto dto) {
        return service.create(dto)
            .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Recepción creada", false, r)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar recepción por id (con líneas)")
    public Mono<ResponseEntity<ApiResponse<RecepcionResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(r -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, r)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar recepciones (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<RecepcionTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar recepción → entra a inventario y (opcional) genera asiento contable")
    public Mono<ResponseEntity<ApiResponse<RecepcionResponseDto>>> confirmar(
            @PathVariable Long id, @RequestBody(required = false) ConfirmarRecepcionRequestDto params) {
        return service.confirmar(id, params != null ? params : new ConfirmarRecepcionRequestDto())
            .map(r -> ResponseEntity.ok(new ApiResponse<>(200, "Recepción confirmada", false, r)));
    }
}
