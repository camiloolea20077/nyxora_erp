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
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroFilterDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.TerceroService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/terceros")
@Tag(name = "Terceros", description = "Maestro de terceros: clientes/proveedores/empleados (Común)")
public class TerceroController {

    private final TerceroService terceroService;

    public TerceroController(TerceroService terceroService) {
        this.terceroService = terceroService;
    }

    @PostMapping
    @Operation(summary = "Crear tercero (con clasificación cliente/proveedor/empleado)")
    public Mono<ResponseEntity<ApiResponse<TerceroResponseDto>>> create(@Valid @RequestBody CreateTerceroRequestDto dto) {
        return terceroService.create(dto)
            .map(t -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Tercero creado", false, t)));
    }

    @PutMapping
    @Operation(summary = "Actualizar tercero (parcial; reemplaza clasificación si se envía)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateTerceroRequestDto dto) {
        return terceroService.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Tercero actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tercero (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return terceroService.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Tercero eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar tercero por id (incluye tipoTerceroIds)")
    public Mono<ResponseEntity<ApiResponse<TerceroResponseDto>>> findById(@PathVariable Long id) {
        return terceroService.findById(id)
            .map(t -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, t)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar terceros (paginado, búsqueda por nombre/documento)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<TerceroTableDto>>>> list(@RequestBody PageableDto<TerceroFilterDto> request) {
        return terceroService.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
