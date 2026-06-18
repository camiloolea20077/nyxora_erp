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
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ConceptoNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ConceptoNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateConceptoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateConceptoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ConceptoNominaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/conceptos-nomina")
@Tag(name = "Conceptos de nómina", description = "Conceptos (devengados, deducciones, provisiones, aportes) con fórmula")
public class ConceptoNominaController {

    private final ConceptoNominaService service;

    public ConceptoNominaController(ConceptoNominaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear concepto de nómina")
    public Mono<ResponseEntity<ApiResponse<ConceptoNominaResponseDto>>> create(@Valid @RequestBody CreateConceptoNominaRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Concepto creado", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar concepto de nómina")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateConceptoNominaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Concepto actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar concepto de nómina (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Concepto eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar concepto por id")
    public Mono<ResponseEntity<ApiResponse<ConceptoNominaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar conceptos (paginado; filtro opcional clase)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ConceptoNominaTableDto>>>> list(
            @RequestBody PageableDto<Void> request,
            @RequestParam(required = false) String clase) {
        return service.list(request, clase)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
