package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

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
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.AddProcesoFaltaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CambiarEstadoProcesoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateProcesoDescargoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateProcesoDisciplinarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateProcesoNotificacionDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDescargoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDisciplinarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDisciplinarioTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoFaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoNotificacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.UpdateProcesoDisciplinarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ProcesoDisciplinarioService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/procesos-disciplinarios")
@Tag(name = "Procesos disciplinarios", description = "Procesos disciplinarios con faltas, descargos y notificaciones")
public class ProcesoDisciplinarioController {

    private final ProcesoDisciplinarioService service;

    public ProcesoDisciplinarioController(ProcesoDisciplinarioService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear proceso disciplinario")
    public Mono<ResponseEntity<ApiResponse<ProcesoDisciplinarioResponseDto>>> create(@Valid @RequestBody CreateProcesoDisciplinarioRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Proceso creado", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar proceso disciplinario")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateProcesoDisciplinarioRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Proceso actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proceso disciplinario (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Proceso eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar proceso por id")
    public Mono<ResponseEntity<ApiResponse<ProcesoDisciplinarioResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar procesos (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ProcesoDisciplinarioTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado del proceso")
    public Mono<ResponseEntity<ApiResponse<ProcesoDisciplinarioResponseDto>>> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody CambiarEstadoProcesoRequestDto dto) {
        return service.cambiarEstado(id, dto)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "Estado actualizado", false, m)));
    }

    // ---------- Faltas ----------
    @GetMapping("/{procesoId}/faltas")
    @Operation(summary = "Listar faltas del proceso")
    public Mono<ResponseEntity<ApiResponse<List<ProcesoFaltaResponseDto>>>> listFaltas(@PathVariable Long procesoId) {
        return service.listFaltas(procesoId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/{procesoId}/faltas")
    @Operation(summary = "Imputar una falta al proceso")
    public Mono<ResponseEntity<ApiResponse<ProcesoFaltaResponseDto>>> addFalta(
            @PathVariable Long procesoId, @Valid @RequestBody AddProcesoFaltaDto dto) {
        return service.addFalta(procesoId, dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Falta imputada", false, m)));
    }

    @DeleteMapping("/{procesoId}/faltas/{id}")
    @Operation(summary = "Quitar una falta del proceso")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> removeFalta(@PathVariable Long procesoId, @PathVariable Long id) {
        return service.removeFalta(procesoId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Falta retirada", false, ok)));
    }

    // ---------- Descargos ----------
    @GetMapping("/{procesoId}/descargos")
    @Operation(summary = "Listar descargos del proceso")
    public Mono<ResponseEntity<ApiResponse<List<ProcesoDescargoResponseDto>>>> listDescargos(@PathVariable Long procesoId) {
        return service.listDescargos(procesoId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/{procesoId}/descargos")
    @Operation(summary = "Agregar un descargo al proceso")
    public Mono<ResponseEntity<ApiResponse<ProcesoDescargoResponseDto>>> addDescargo(
            @PathVariable Long procesoId, @Valid @RequestBody CreateProcesoDescargoDto dto) {
        return service.addDescargo(procesoId, dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Descargo agregado", false, m)));
    }

    @DeleteMapping("/{procesoId}/descargos/{id}")
    @Operation(summary = "Eliminar un descargo del proceso")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> removeDescargo(@PathVariable Long procesoId, @PathVariable Long id) {
        return service.removeDescargo(procesoId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Descargo eliminado", false, ok)));
    }

    // ---------- Notificaciones ----------
    @GetMapping("/{procesoId}/notificaciones")
    @Operation(summary = "Listar notificaciones del proceso")
    public Mono<ResponseEntity<ApiResponse<List<ProcesoNotificacionResponseDto>>>> listNotificaciones(@PathVariable Long procesoId) {
        return service.listNotificaciones(procesoId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/{procesoId}/notificaciones")
    @Operation(summary = "Agregar una notificación al proceso")
    public Mono<ResponseEntity<ApiResponse<ProcesoNotificacionResponseDto>>> addNotificacion(
            @PathVariable Long procesoId, @Valid @RequestBody CreateProcesoNotificacionDto dto) {
        return service.addNotificacion(procesoId, dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Notificación agregada", false, m)));
    }

    @DeleteMapping("/{procesoId}/notificaciones/{id}")
    @Operation(summary = "Eliminar una notificación del proceso")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> removeNotificacion(@PathVariable Long procesoId, @PathVariable Long id) {
        return service.removeNotificacion(procesoId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Notificación eliminada", false, ok)));
    }
}
