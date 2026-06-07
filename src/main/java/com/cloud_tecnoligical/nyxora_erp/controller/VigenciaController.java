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
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.CreateVigenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.UpdateVigenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.VigenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.VigenciaTableDto;
import com.cloud_tecnoligical.nyxora_erp.service.VigenciaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/vigencias")
@Tag(name = "Vigencias", description = "Periodos fiscales y su apertura/cierre (Administración)")
public class VigenciaController {

    private final VigenciaService vigenciaService;

    public VigenciaController(VigenciaService vigenciaService) {
        this.vigenciaService = vigenciaService;
    }

    @PostMapping
    @Operation(summary = "Crear vigencia (estado inicial 'planeada')")
    public Mono<ResponseEntity<ApiResponse<VigenciaResponseDto>>> create(@Valid @RequestBody CreateVigenciaRequestDto dto) {
        return vigenciaService.create(dto)
            .map(v -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Vigencia creada", false, v)));
    }

    @PutMapping
    @Operation(summary = "Actualizar vigencia (año)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateVigenciaRequestDto dto) {
        return vigenciaService.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Vigencia actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vigencia (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return vigenciaService.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Vigencia eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar vigencia por id")
    public Mono<ResponseEntity<ApiResponse<VigenciaResponseDto>>> findById(@PathVariable Long id) {
        return vigenciaService.findById(id)
            .map(v -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, v)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar vigencias (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<VigenciaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return vigenciaService.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/abrir")
    @Operation(summary = "Abrir la vigencia (planeada → abierta)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> abrir(@PathVariable Long id) {
        return vigenciaService.abrir(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Vigencia abierta", false, ok)));
    }

    @PostMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar la vigencia (abierta → cerrada)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> cerrar(@PathVariable Long id) {
        return vigenciaService.cerrar(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Vigencia cerrada", false, ok)));
    }
}
