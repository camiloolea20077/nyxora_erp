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
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.CreateRecursoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.RecursoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.RecursoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.UpdateRecursoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.RecursoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/recursos")
@Tag(name = "Recursos", description = "Catálogo de recursos de costeo (Costos)")
public class RecursoController {

    private final RecursoService service;

    public RecursoController(RecursoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear recurso")
    public Mono<ResponseEntity<ApiResponse<RecursoResponseDto>>> create(@Valid @RequestBody CreateRecursoRequestDto dto) {
        return service.create(dto)
            .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Recurso creado", false, r)));
    }

    @PutMapping
    @Operation(summary = "Actualizar recurso")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateRecursoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Recurso actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar recurso (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Recurso eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar recurso por id")
    public Mono<ResponseEntity<ApiResponse<RecursoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(r -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, r)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar recursos (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<RecursoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
