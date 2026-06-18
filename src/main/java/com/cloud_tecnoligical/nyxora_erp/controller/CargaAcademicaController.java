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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaAcademicaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaAcademicaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateCargaAcademicaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GenerarNovedadDocenteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateCargaAcademicaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.CargaAcademicaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cargas-academicas")
@Tag(name = "Carga académica", description = "Carga docente y traslado a nómina (catedráticos)")
public class CargaAcademicaController {

    private final CargaAcademicaService service;

    public CargaAcademicaController(CargaAcademicaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear carga académica (con detalle)")
    public Mono<ResponseEntity<ApiResponse<CargaAcademicaResponseDto>>> create(@Valid @RequestBody CreateCargaAcademicaRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Carga creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar carga académica (reemplaza el detalle)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateCargaAcademicaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Carga actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar carga académica (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Carga eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar carga (con detalle)")
    public Mono<ResponseEntity<ApiResponse<CargaAcademicaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar cargas (paginado; filtro opcional vinculacionId)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<CargaAcademicaTableDto>>>> list(
            @RequestBody PageableDto<Void> request,
            @RequestParam(required = false) Long vinculacionId) {
        return service.list(request, vinculacionId)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/generar-novedad")
    @Operation(summary = "Trasladar la carga docente a nómina (evento → novedad por horas)")
    public Mono<ResponseEntity<ApiResponse<CargaAcademicaResponseDto>>> generarNovedad(
            @PathVariable Long id, @Valid @RequestBody GenerarNovedadDocenteRequestDto dto) {
        return service.generarNovedad(id, dto)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "Novedad de nómina solicitada", false, m)));
    }
}
