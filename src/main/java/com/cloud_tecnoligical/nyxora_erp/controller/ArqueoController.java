package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.ArqueoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ArqueoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateArqueoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.ArqueoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/arqueos")
@Tag(name = "Arqueos", description = "Cuadre de caja: valor declarado vs sistema (Caja)")
public class ArqueoController {

    private final ArqueoService service;

    public ArqueoController(ArqueoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear arqueo (calcula valor del sistema y la diferencia)")
    public Mono<ResponseEntity<ApiResponse<ArqueoResponseDto>>> create(@Valid @RequestBody CreateArqueoRequestDto dto) {
        return service.create(dto)
            .map(a -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Arqueo registrado", false, a)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar arqueo por id")
    public Mono<ResponseEntity<ApiResponse<ArqueoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(a -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, a)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar arqueos (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ArqueoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
