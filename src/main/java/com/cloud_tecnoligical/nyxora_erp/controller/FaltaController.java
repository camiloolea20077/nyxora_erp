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
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.FaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.FaltaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.UpdateFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.FaltaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/faltas")
@Tag(name = "Faltas", description = "Catálogo de faltas disciplinarias")
public class FaltaController {

    private final FaltaService service;

    public FaltaController(FaltaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear falta")
    public Mono<ResponseEntity<ApiResponse<FaltaResponseDto>>> create(@Valid @RequestBody CreateFaltaRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Falta creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar falta")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateFaltaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Falta actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar falta (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Falta eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar falta por id")
    public Mono<ResponseEntity<ApiResponse<FaltaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar faltas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<FaltaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
