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
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateCuentaBancariaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CuentaBancariaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.UpdateCuentaBancariaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.CuentaBancariaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cuentas-bancarias")
@Tag(name = "Cuentas bancarias", description = "Cuentas bancarias propias de la empresa (Tesorería)")
public class CuentaBancariaController {

    private final CuentaBancariaService service;

    public CuentaBancariaController(CuentaBancariaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear cuenta bancaria")
    public Mono<ResponseEntity<ApiResponse<CuentaBancariaResponseDto>>> create(@Valid @RequestBody CreateCuentaBancariaRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Cuenta bancaria creada", false, c)));
    }

    @PutMapping
    @Operation(summary = "Actualizar cuenta bancaria")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateCuentaBancariaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Cuenta bancaria actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cuenta bancaria (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Cuenta bancaria eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar cuenta bancaria por id")
    public Mono<ResponseEntity<ApiResponse<CuentaBancariaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar cuentas bancarias (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<CuentaBancariaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
