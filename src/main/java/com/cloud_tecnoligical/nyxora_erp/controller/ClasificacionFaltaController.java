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
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ClasificacionFaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ClasificacionFaltaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateClasificacionFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.UpdateClasificacionFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ClasificacionFaltaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clasificaciones-falta")
@Tag(name = "Clasificaciones de falta", description = "Catálogo de clasificaciones de faltas")
public class ClasificacionFaltaController {

    private final ClasificacionFaltaService service;

    public ClasificacionFaltaController(ClasificacionFaltaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear clasificación de falta")
    public Mono<ResponseEntity<ApiResponse<ClasificacionFaltaResponseDto>>> create(@Valid @RequestBody CreateClasificacionFaltaRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Clasificación creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar clasificación de falta")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateClasificacionFaltaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Clasificación actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar clasificación de falta (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Clasificación eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar clasificación por id")
    public Mono<ResponseEntity<ApiResponse<ClasificacionFaltaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar clasificaciones (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ClasificacionFaltaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
