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
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateCuentaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CuentaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CuentaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.UpdateCuentaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.CuentaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cuentas")
@Tag(name = "Cuentas", description = "Plan de cuentas contable jerárquico (Contabilidad)")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear cuenta")
    public Mono<ResponseEntity<ApiResponse<CuentaResponseDto>>> create(@Valid @RequestBody CreateCuentaRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Cuenta creada", false, c)));
    }

    @PutMapping
    @Operation(summary = "Actualizar cuenta")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateCuentaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Cuenta actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cuenta (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Cuenta eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar cuenta por id")
    public Mono<ResponseEntity<ApiResponse<CuentaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar cuentas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<CuentaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
