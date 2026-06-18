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
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEvaluacionProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionProgramaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionProgramaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEvaluacionProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.service.EvaluacionProgramaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/evaluacion-programas")
@Tag(name = "Evaluación - Programas", description = "Programas de evaluación de desempeño")
public class EvaluacionProgramaController {

    private final EvaluacionProgramaService service;

    public EvaluacionProgramaController(EvaluacionProgramaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear programa de evaluación")
    public Mono<ResponseEntity<ApiResponse<EvaluacionProgramaResponseDto>>> create(@Valid @RequestBody CreateEvaluacionProgramaDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Programa creado", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar programa de evaluación")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateEvaluacionProgramaDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Programa actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar programa de evaluación (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Programa eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar programa por id")
    public Mono<ResponseEntity<ApiResponse<EvaluacionProgramaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar programas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<EvaluacionProgramaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
