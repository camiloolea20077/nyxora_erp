package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CreateCuentaPorCobrarRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CuentaPorCobrarResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CuentaPorCobrarTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.CuentaPorCobrarService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cuentas-cobrar")
@Tag(name = "Cuentas por cobrar", description = "Cartera: cuentas por cobrar (Cartera)")
public class CuentaPorCobrarController {

    private final CuentaPorCobrarService service;

    public CuentaPorCobrarController(CuentaPorCobrarService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear cuenta por cobrar manual (saldo inicial)")
    public Mono<ResponseEntity<ApiResponse<CuentaPorCobrarResponseDto>>> create(@Valid @RequestBody CreateCuentaPorCobrarRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Cuenta por cobrar creada", false, c)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar cuenta por cobrar por id")
    public Mono<ResponseEntity<ApiResponse<CuentaPorCobrarResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar cuentas por cobrar (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<CuentaPorCobrarTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
