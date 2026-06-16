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
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ChequeraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ChequeraTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateChequeraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.UpdateChequeraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ChequeraService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chequeras")
@Tag(name = "Chequeras", description = "Chequeras por cuenta bancaria (Tesorería)")
public class ChequeraController {

    private final ChequeraService service;

    public ChequeraController(ChequeraService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear chequera")
    public Mono<ResponseEntity<ApiResponse<ChequeraResponseDto>>> create(@Valid @RequestBody CreateChequeraRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Chequera creada", false, c)));
    }

    @PutMapping
    @Operation(summary = "Actualizar chequera")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateChequeraRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Chequera actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar chequera (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Chequera eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar chequera por id")
    public Mono<ResponseEntity<ApiResponse<ChequeraResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar chequeras (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ChequeraTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
