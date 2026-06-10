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
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateMarcaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MarcaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MarcaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateMarcaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.MarcaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/marcas")
@Tag(name = "Marcas", description = "Marcas de producto (Inventario)")
public class MarcaController {

    private final MarcaService service;

    public MarcaController(MarcaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear marca")
    public Mono<ResponseEntity<ApiResponse<MarcaResponseDto>>> create(@Valid @RequestBody CreateMarcaRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Marca creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar marca")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateMarcaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Marca actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar marca (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Marca eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar marca por id")
    public Mono<ResponseEntity<ApiResponse<MarcaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar marcas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<MarcaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
