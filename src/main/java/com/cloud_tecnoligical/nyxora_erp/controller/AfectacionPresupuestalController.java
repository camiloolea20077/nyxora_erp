package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.AfectacionPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.AfectacionPresupuestalTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateAfectacionPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.AfectacionPresupuestalService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/afectaciones-presupuestales")
@Tag(name = "Afectaciones presupuestales", description = "Cadena CDP→compromiso→obligación→pago (Presupuesto)")
public class AfectacionPresupuestalController {

    private final AfectacionPresupuestalService service;

    public AfectacionPresupuestalController(AfectacionPresupuestalService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar afectación presupuestal (append-only)")
    public Mono<ResponseEntity<ApiResponse<AfectacionPresupuestalResponseDto>>> registrar(@Valid @RequestBody CreateAfectacionPresupuestalRequestDto dto) {
        return service.registrar(dto)
            .map(a -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Afectación registrada", false, a)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar afectación por id")
    public Mono<ResponseEntity<ApiResponse<AfectacionPresupuestalResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(a -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, a)));
    }

    @PostMapping("/rubro/{rubroId}/list")
    @Operation(summary = "Listar afectaciones de un rubro (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<AfectacionPresupuestalTableDto>>>> list(
            @PathVariable Long rubroId, @RequestBody PageableDto<Void> request) {
        return service.listByRubro(rubroId, request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
