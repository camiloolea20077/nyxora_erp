package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.CreateEmpresaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.EmpresaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.EmpresaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.UpdateEmpresaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.EmpresaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/empresas")
@Tag(name = "Empresas", description = "Gestión de empresas / tenant (Administración)")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    @Operation(summary = "Crear empresa (solo super-admin)")
    public Mono<ResponseEntity<ApiResponse<EmpresaResponseDto>>> create(@Valid @RequestBody CreateEmpresaRequestDto dto) {
        return empresaService.create(dto)
            .map(e -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Empresa creada", false, e)));
    }

    @PutMapping
    @Operation(summary = "Actualizar empresa (la propia, o cualquiera si super-admin)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateEmpresaRequestDto dto) {
        return empresaService.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Empresa actualizada", false, ok)));
    }

    @GetMapping("/actual")
    @Operation(summary = "Consultar la empresa del usuario autenticado")
    public Mono<ResponseEntity<ApiResponse<EmpresaResponseDto>>> findActual() {
        return empresaService.findActual()
            .map(e -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, e)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar empresa por id (la propia, o cualquiera si super-admin)")
    public Mono<ResponseEntity<ApiResponse<EmpresaResponseDto>>> findById(@PathVariable Long id) {
        return empresaService.findById(id)
            .map(e -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, e)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar empresas (solo super-admin)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<EmpresaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return empresaService.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
