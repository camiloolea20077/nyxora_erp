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
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.AsignarRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.CreateUsuarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UpdateUsuarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UsuarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UsuarioTableDto;
import com.cloud_tecnoligical.nyxora_erp.service.UsuarioService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios y asignación de roles (Administración)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    public Mono<ResponseEntity<ApiResponse<UsuarioResponseDto>>> create(@Valid @RequestBody CreateUsuarioRequestDto dto) {
        return usuarioService.create(dto)
            .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Usuario creado", false, u)));
    }

    @PutMapping
    @Operation(summary = "Actualizar usuario (email/activo/contraseña opcional)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateUsuarioRequestDto dto) {
        return usuarioService.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Usuario actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return usuarioService.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Usuario eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuario por id")
    public Mono<ResponseEntity<ApiResponse<UsuarioResponseDto>>> findById(@PathVariable Long id) {
        return usuarioService.findById(id)
            .map(u -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, u)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar usuarios (paginado, búsqueda)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<UsuarioTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return usuarioService.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "Asignar un rol al usuario en una sede")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> asignarRol(@PathVariable Long id,
            @Valid @RequestBody AsignarRolRequestDto dto) {
        return usuarioService.asignarRol(id, dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Rol asignado", false, ok)));
    }

    @DeleteMapping("/{id}/roles")
    @Operation(summary = "Quitar un rol del usuario en una sede")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> quitarRol(@PathVariable Long id,
            @Valid @RequestBody AsignarRolRequestDto dto) {
        return usuarioService.quitarRol(id, dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Rol retirado", false, ok)));
    }
}
