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

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.AsignarPolizaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.AsignarResponsableRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreateActivoFijoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.UpdateActivoFijoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.ActivoFijoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/activos-fijos")
@Tag(name = "Activos fijos", description = "Activos fijos, responsables y pólizas (Activos Fijos)")
public class ActivoFijoController {

    private final ActivoFijoService service;

    public ActivoFijoController(ActivoFijoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear activo fijo")
    public Mono<ResponseEntity<ApiResponse<ActivoFijoResponseDto>>> create(@Valid @RequestBody CreateActivoFijoRequestDto dto) {
        return service.create(dto)
            .map(a -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Activo fijo creado", false, a)));
    }

    @PutMapping
    @Operation(summary = "Actualizar activo fijo")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateActivoFijoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Activo fijo actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar activo fijo (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Activo fijo eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar activo fijo por id (con responsables y pólizas)")
    public Mono<ResponseEntity<ApiResponse<ActivoFijoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(a -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, a)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar activos fijos (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ActivoFijoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/responsables")
    @Operation(summary = "Asignar responsable al activo fijo")
    public Mono<ResponseEntity<ApiResponse<ActivoFijoResponseDto>>> asignarResponsable(
            @PathVariable Long id, @Valid @RequestBody AsignarResponsableRequestDto dto) {
        return service.asignarResponsable(id, dto)
            .map(a -> ResponseEntity.ok(new ApiResponse<>(200, "Responsable asignado", false, a)));
    }

    @DeleteMapping("/{id}/responsables/{terceroId}")
    @Operation(summary = "Remover responsable del activo fijo")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> removerResponsable(
            @PathVariable Long id, @PathVariable Long terceroId) {
        return service.removerResponsable(id, terceroId)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Responsable removido", false, ok)));
    }

    @PostMapping("/{id}/polizas")
    @Operation(summary = "Asignar póliza al activo fijo")
    public Mono<ResponseEntity<ApiResponse<ActivoFijoResponseDto>>> asignarPoliza(
            @PathVariable Long id, @Valid @RequestBody AsignarPolizaRequestDto dto) {
        return service.asignarPoliza(id, dto)
            .map(a -> ResponseEntity.ok(new ApiResponse<>(200, "Póliza asignada", false, a)));
    }

    @DeleteMapping("/{id}/polizas/{polizaSeguroId}")
    @Operation(summary = "Remover póliza del activo fijo")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> removerPoliza(
            @PathVariable Long id, @PathVariable Long polizaSeguroId) {
        return service.removerPoliza(id, polizaSeguroId)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Póliza removida", false, ok)));
    }
}
