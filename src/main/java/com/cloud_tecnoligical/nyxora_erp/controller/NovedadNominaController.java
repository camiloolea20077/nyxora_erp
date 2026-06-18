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
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateNovedadNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.NovedadNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.NovedadNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateNovedadNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.NovedadNominaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/novedades-nomina")
@Tag(name = "Novedades de nómina", description = "Novedades del periodo (incl. embargos)")
public class NovedadNominaController {

    private final NovedadNominaService service;

    public NovedadNominaController(NovedadNominaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear novedad de nómina")
    public Mono<ResponseEntity<ApiResponse<NovedadNominaResponseDto>>> create(@Valid @RequestBody CreateNovedadNominaRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Novedad creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar novedad de nómina")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateNovedadNominaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Novedad actualizada", false, ok)));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular novedad de nómina")
    public Mono<ResponseEntity<ApiResponse<NovedadNominaResponseDto>>> anular(@PathVariable Long id) {
        return service.anular(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "Novedad anulada", false, m)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar novedad de nómina (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Novedad eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar novedad por id")
    public Mono<ResponseEntity<ApiResponse<NovedadNominaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar novedades (paginado; filtro opcional vinculacionId)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<NovedadNominaTableDto>>>> list(
            @RequestBody PageableDto<Void> request,
            @RequestParam(required = false) Long vinculacionId) {
        return service.list(request, vinculacionId)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
