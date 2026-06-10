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
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateLoteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.LoteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.LoteTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateLoteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.LoteService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/lotes")
@Tag(name = "Lotes", description = "Lotes de inventario (Inventario)")
public class LoteController {

    private final LoteService service;

    public LoteController(LoteService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear lote")
    public Mono<ResponseEntity<ApiResponse<LoteResponseDto>>> create(@Valid @RequestBody CreateLoteRequestDto dto) {
        return service.create(dto)
            .map(l -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Lote creado", false, l)));
    }

    @PutMapping
    @Operation(summary = "Actualizar lote")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateLoteRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Lote actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar lote (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Lote eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar lote por id")
    public Mono<ResponseEntity<ApiResponse<LoteResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar lotes (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<LoteTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
