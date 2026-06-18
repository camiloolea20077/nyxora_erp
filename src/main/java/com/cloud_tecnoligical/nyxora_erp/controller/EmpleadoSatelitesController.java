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

import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoEstudioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoFamiliarDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoHistoriaLaboralDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoEstudioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoFamiliarResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoHistoriaLaboralResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoEstudioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoFamiliarDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoHistoriaLaboralDto;
import com.cloud_tecnoligical.nyxora_erp.service.EmpleadoSatelitesService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/empleados/{empleadoId}")
@Tag(name = "Empleados - Satélites", description = "Estudios, familiares e historia laboral del empleado (tercero)")
public class EmpleadoSatelitesController {

    private final EmpleadoSatelitesService service;

    public EmpleadoSatelitesController(EmpleadoSatelitesService service) {
        this.service = service;
    }

    // ---------- Estudios ----------
    @GetMapping("/estudios")
    @Operation(summary = "Listar estudios del empleado")
    public Mono<ResponseEntity<ApiResponse<List<EmpleadoEstudioResponseDto>>>> listEstudios(@PathVariable Long empleadoId) {
        return service.listEstudios(empleadoId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/estudios")
    @Operation(summary = "Agregar estudio al empleado")
    public Mono<ResponseEntity<ApiResponse<EmpleadoEstudioResponseDto>>> createEstudio(
            @PathVariable Long empleadoId, @Valid @RequestBody CreateEmpleadoEstudioDto dto) {
        return service.createEstudio(empleadoId, dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Estudio creado", false, c)));
    }

    @PutMapping("/estudios")
    @Operation(summary = "Actualizar estudio del empleado")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> updateEstudio(
            @PathVariable Long empleadoId, @Valid @RequestBody UpdateEmpleadoEstudioDto dto) {
        return service.updateEstudio(empleadoId, dto).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Estudio actualizado", false, ok)));
    }

    @DeleteMapping("/estudios/{id}")
    @Operation(summary = "Eliminar estudio del empleado")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> deleteEstudio(@PathVariable Long empleadoId, @PathVariable Long id) {
        return service.deleteEstudio(empleadoId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Estudio eliminado", false, ok)));
    }

    // ---------- Familiares ----------
    @GetMapping("/familiares")
    @Operation(summary = "Listar familiares del empleado")
    public Mono<ResponseEntity<ApiResponse<List<EmpleadoFamiliarResponseDto>>>> listFamiliares(@PathVariable Long empleadoId) {
        return service.listFamiliares(empleadoId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/familiares")
    @Operation(summary = "Agregar familiar al empleado")
    public Mono<ResponseEntity<ApiResponse<EmpleadoFamiliarResponseDto>>> createFamiliar(
            @PathVariable Long empleadoId, @Valid @RequestBody CreateEmpleadoFamiliarDto dto) {
        return service.createFamiliar(empleadoId, dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Familiar creado", false, c)));
    }

    @PutMapping("/familiares")
    @Operation(summary = "Actualizar familiar del empleado")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> updateFamiliar(
            @PathVariable Long empleadoId, @Valid @RequestBody UpdateEmpleadoFamiliarDto dto) {
        return service.updateFamiliar(empleadoId, dto).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Familiar actualizado", false, ok)));
    }

    @DeleteMapping("/familiares/{id}")
    @Operation(summary = "Eliminar familiar del empleado")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> deleteFamiliar(@PathVariable Long empleadoId, @PathVariable Long id) {
        return service.deleteFamiliar(empleadoId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Familiar eliminado", false, ok)));
    }

    // ---------- Historia laboral ----------
    @GetMapping("/historias-laborales")
    @Operation(summary = "Listar historia laboral del empleado")
    public Mono<ResponseEntity<ApiResponse<List<EmpleadoHistoriaLaboralResponseDto>>>> listHistorias(@PathVariable Long empleadoId) {
        return service.listHistorias(empleadoId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/historias-laborales")
    @Operation(summary = "Agregar historia laboral al empleado")
    public Mono<ResponseEntity<ApiResponse<EmpleadoHistoriaLaboralResponseDto>>> createHistoria(
            @PathVariable Long empleadoId, @Valid @RequestBody CreateEmpleadoHistoriaLaboralDto dto) {
        return service.createHistoria(empleadoId, dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Historia laboral creada", false, c)));
    }

    @PutMapping("/historias-laborales")
    @Operation(summary = "Actualizar historia laboral del empleado")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> updateHistoria(
            @PathVariable Long empleadoId, @Valid @RequestBody UpdateEmpleadoHistoriaLaboralDto dto) {
        return service.updateHistoria(empleadoId, dto).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Historia laboral actualizada", false, ok)));
    }

    @DeleteMapping("/historias-laborales/{id}")
    @Operation(summary = "Eliminar historia laboral del empleado")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> deleteHistoria(@PathVariable Long empleadoId, @PathVariable Long id) {
        return service.deleteHistoria(empleadoId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Historia laboral eliminada", false, ok)));
    }
}
