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
import com.cloud_tecnoligical.nyxora_erp.dto.rol.CreateRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.RolResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.RolTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.UpdateRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.RolService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Gestión de roles y sus permisos (Administración)")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    @Operation(summary = "Crear rol (con permisos opcionales)")
    public Mono<ResponseEntity<ApiResponse<RolResponseDto>>> create(@Valid @RequestBody CreateRolRequestDto dto) {
        return rolService.create(dto)
            .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Rol creado", false, r)));
    }

    @PutMapping
    @Operation(summary = "Actualizar rol (y reemplazar permisos si se envían)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateRolRequestDto dto) {
        return rolService.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Rol actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return rolService.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Rol eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar rol por id (incluye permisoIds)")
    public Mono<ResponseEntity<ApiResponse<RolResponseDto>>> findById(@PathVariable Long id) {
        return rolService.findById(id)
            .map(r -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, r)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar roles (paginado, búsqueda)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<RolTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return rolService.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
