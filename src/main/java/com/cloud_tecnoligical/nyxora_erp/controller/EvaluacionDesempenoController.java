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

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEvaluacionDesempenoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionDesempenoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionDesempenoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEvaluacionDesempenoDto;
import com.cloud_tecnoligical.nyxora_erp.service.EvaluacionDesempenoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/evaluaciones-desempeno")
@Tag(name = "Evaluación - Desempeño", description = "Evaluaciones de desempeño de empleados")
public class EvaluacionDesempenoController {

    private final EvaluacionDesempenoService service;

    public EvaluacionDesempenoController(EvaluacionDesempenoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear evaluación de desempeño")
    public Mono<ResponseEntity<ApiResponse<EvaluacionDesempenoResponseDto>>> create(@Valid @RequestBody CreateEvaluacionDesempenoDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Evaluación creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar evaluación de desempeño")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateEvaluacionDesempenoDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Evaluación actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evaluación de desempeño (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Evaluación eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar evaluación por id")
    public Mono<ResponseEntity<ApiResponse<EvaluacionDesempenoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar evaluaciones (paginado; filtros opcionales empleadoId/programaId)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<EvaluacionDesempenoTableDto>>>> list(
            @RequestBody PageableDto<Void> request,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) Long programaId) {
        return service.list(request, empleadoId, programaId)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
