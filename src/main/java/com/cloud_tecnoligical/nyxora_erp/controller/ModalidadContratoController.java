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
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateModalidadContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ModalidadContratoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ModalidadContratoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.UpdateModalidadContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ModalidadContratoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/modalidades-contrato")
@Tag(name = "Modalidades de contrato", description = "Catálogo de modalidades de contratación")
public class ModalidadContratoController {

    private final ModalidadContratoService service;

    public ModalidadContratoController(ModalidadContratoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear modalidad de contrato")
    public Mono<ResponseEntity<ApiResponse<ModalidadContratoResponseDto>>> create(@Valid @RequestBody CreateModalidadContratoRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Modalidad creada", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar modalidad de contrato")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateModalidadContratoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Modalidad actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar modalidad de contrato (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Modalidad eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar modalidad por id")
    public Mono<ResponseEntity<ApiResponse<ModalidadContratoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar modalidades (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ModalidadContratoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
