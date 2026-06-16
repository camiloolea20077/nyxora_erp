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
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CpcResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CpcTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateCpcRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.UpdateCpcRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.CpcService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cpc")
@Tag(name = "CPC", description = "Clasificador de Productos y servicios presupuestal (Presupuesto)")
public class CpcController {

    private final CpcService service;

    public CpcController(CpcService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear CPC")
    public Mono<ResponseEntity<ApiResponse<CpcResponseDto>>> create(@Valid @RequestBody CreateCpcRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "CPC creado", false, c)));
    }

    @PutMapping
    @Operation(summary = "Actualizar CPC")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateCpcRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "CPC actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar CPC (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "CPC eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar CPC por id")
    public Mono<ResponseEntity<ApiResponse<CpcResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar CPC (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<CpcTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
