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
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateRubroPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.RubroPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.RubroPresupuestalTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.UpdateRubroPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.RubroPresupuestalService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/rubros-presupuestales")
@Tag(name = "Rubros presupuestales", description = "Definición de rubros presupuestales (Presupuesto)")
public class RubroPresupuestalController {

    private final RubroPresupuestalService service;

    public RubroPresupuestalController(RubroPresupuestalService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear rubro presupuestal")
    public Mono<ResponseEntity<ApiResponse<RubroPresupuestalResponseDto>>> create(@Valid @RequestBody CreateRubroPresupuestalRequestDto dto) {
        return service.create(dto)
            .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Rubro creado", false, r)));
    }

    @PutMapping
    @Operation(summary = "Actualizar rubro presupuestal")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateRubroPresupuestalRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Rubro actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rubro presupuestal (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Rubro eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar rubro por id")
    public Mono<ResponseEntity<ApiResponse<RubroPresupuestalResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(r -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, r)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar rubros (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<RubroPresupuestalTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
