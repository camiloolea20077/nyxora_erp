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
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CentroCostoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CentroCostoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateCentroCostoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateCentroCostoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.CentroCostoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/centros-costo")
@Tag(name = "Centros de costo", description = "Centros de costo jerárquicos (Común)")
public class CentroCostoController {

    private final CentroCostoService service;

    public CentroCostoController(CentroCostoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear centro de costo")
    public Mono<ResponseEntity<ApiResponse<CentroCostoResponseDto>>> create(@Valid @RequestBody CreateCentroCostoRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Centro de costo creado", false, c)));
    }

    @PutMapping
    @Operation(summary = "Actualizar centro de costo")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateCentroCostoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Centro de costo actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar centro de costo (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Centro de costo eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar centro de costo por id")
    public Mono<ResponseEntity<ApiResponse<CentroCostoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar centros de costo (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<CentroCostoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
