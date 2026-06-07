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
import com.cloud_tecnoligical.nyxora_erp.dto.sede.CreateSedeRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.SedeResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.SedeTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.UpdateSedeRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.SedeService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/sedes")
@Tag(name = "Sedes", description = "Gestión de sedes (módulo Administración)")
public class SedeController {

    private final SedeService sedeService;

    public SedeController(SedeService sedeService) {
        this.sedeService = sedeService;
    }

    @PostMapping
    @Operation(summary = "Crear sede")
    public Mono<ResponseEntity<ApiResponse<SedeResponseDto>>> create(@Valid @RequestBody CreateSedeRequestDto dto) {
        return sedeService.create(dto)
            .map(s -> ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Sede creada", false, s)));
    }

    @PutMapping
    @Operation(summary = "Actualizar sede")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateSedeRequestDto dto) {
        return sedeService.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Sede actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sede (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return sedeService.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Sede eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar sede por id")
    public Mono<ResponseEntity<ApiResponse<SedeResponseDto>>> findById(@PathVariable Long id) {
        return sedeService.findById(id)
            .map(s -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, s)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar sedes (paginado, búsqueda)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<SedeTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return sedeService.list(request)
            .map(page -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, page)));
    }
}
