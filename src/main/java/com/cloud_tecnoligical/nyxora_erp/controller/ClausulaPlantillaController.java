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
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ClausulaPlantillaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ClausulaPlantillaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateClausulaPlantillaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.UpdateClausulaPlantillaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ClausulaPlantillaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clausulas-plantilla")
@Tag(name = "Plantillas de cláusula", description = "Catálogo de plantillas de cláusulas contractuales")
public class ClausulaPlantillaController {

    private final ClausulaPlantillaService service;

    public ClausulaPlantillaController(ClausulaPlantillaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear plantilla de cláusula")
    public Mono<ResponseEntity<ApiResponse<ClausulaPlantillaResponseDto>>> create(@Valid @RequestBody CreateClausulaPlantillaRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Cláusula creada", false, c)));
    }

    @PutMapping
    @Operation(summary = "Actualizar plantilla de cláusula")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateClausulaPlantillaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Cláusula actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar plantilla de cláusula (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Cláusula eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar plantilla de cláusula por id")
    public Mono<ResponseEntity<ApiResponse<ClausulaPlantillaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar plantillas de cláusula (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ClausulaPlantillaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
