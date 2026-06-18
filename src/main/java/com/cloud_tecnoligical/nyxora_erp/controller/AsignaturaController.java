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

import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaProgramaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateAsignaturaProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateAsignaturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateAsignaturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.AsignaturaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/asignaturas")
@Tag(name = "Asignaturas", description = "Asignaturas y su asociación a programas")
public class AsignaturaController {

    private final AsignaturaService service;

    public AsignaturaController(AsignaturaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear asignatura")
    public Mono<ResponseEntity<ApiResponse<AsignaturaResponseDto>>> create(@Valid @RequestBody CreateAsignaturaRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Asignatura creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar asignatura")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateAsignaturaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Asignatura actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asignatura (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Asignatura eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar asignatura por id")
    public Mono<ResponseEntity<ApiResponse<AsignaturaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar asignaturas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<AsignaturaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    // ---------- Programas de la asignatura ----------
    @GetMapping("/{asignaturaId}/programas")
    @Operation(summary = "Listar programas de la asignatura")
    public Mono<ResponseEntity<ApiResponse<List<AsignaturaProgramaResponseDto>>>> listProgramas(@PathVariable Long asignaturaId) {
        return service.listProgramas(asignaturaId)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/{asignaturaId}/programas")
    @Operation(summary = "Asociar la asignatura a un programa")
    public Mono<ResponseEntity<ApiResponse<AsignaturaProgramaResponseDto>>> addPrograma(
            @PathVariable Long asignaturaId, @Valid @RequestBody CreateAsignaturaProgramaDto dto) {
        return service.addPrograma(asignaturaId, dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Programa asociado", false, m)));
    }

    @DeleteMapping("/{asignaturaId}/programas/{enlaceId}")
    @Operation(summary = "Quitar la asignatura de un programa")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> removePrograma(@PathVariable Long asignaturaId, @PathVariable Long enlaceId) {
        return service.removePrograma(asignaturaId, enlaceId)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Programa desasociado", false, ok)));
    }
}
