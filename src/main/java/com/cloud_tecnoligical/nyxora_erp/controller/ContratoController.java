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
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.AsignarPolizaContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CambiarEstadoContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.UpdateContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ContratoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/contratos")
@Tag(name = "Contratos", description = "Contratos, cláusulas y pólizas (Contratación)")
public class ContratoController {

    private final ContratoService service;

    public ContratoController(ContratoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear contrato (con cláusulas)")
    public Mono<ResponseEntity<ApiResponse<ContratoResponseDto>>> create(@Valid @RequestBody CreateContratoRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Contrato creado", false, c)));
    }

    @PutMapping
    @Operation(summary = "Actualizar contrato (reemplaza cláusulas)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateContratoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Contrato actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar contrato (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Contrato eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar contrato por id (con cláusulas y pólizas)")
    public Mono<ResponseEntity<ApiResponse<ContratoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar contratos (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ContratoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado del contrato")
    public Mono<ResponseEntity<ApiResponse<ContratoResponseDto>>> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody CambiarEstadoContratoRequestDto dto) {
        return service.cambiarEstado(id, dto)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "Estado actualizado", false, c)));
    }

    @PostMapping("/{id}/polizas")
    @Operation(summary = "Asignar póliza al contrato")
    public Mono<ResponseEntity<ApiResponse<ContratoResponseDto>>> asignarPoliza(
            @PathVariable Long id, @Valid @RequestBody AsignarPolizaContratoRequestDto dto) {
        return service.asignarPoliza(id, dto)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "Póliza asignada", false, c)));
    }

    @DeleteMapping("/{id}/polizas/{polizaSeguroId}")
    @Operation(summary = "Remover póliza del contrato")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> removerPoliza(
            @PathVariable Long id, @PathVariable Long polizaSeguroId) {
        return service.removerPoliza(id, polizaSeguroId)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Póliza removida", false, ok)));
    }
}
