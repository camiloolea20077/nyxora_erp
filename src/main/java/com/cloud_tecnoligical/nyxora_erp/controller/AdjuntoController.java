package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.adjunto.AdjuntoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.adjunto.CreateAdjuntoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.AdjuntoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/adjuntos")
@Tag(name = "Adjuntos", description = "Archivos adjuntos polimórficos por (modulo, entidad, entidadId)")
public class AdjuntoController {

    private final AdjuntoService service;

    public AdjuntoController(AdjuntoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar un adjunto para una entidad")
    public Mono<ResponseEntity<ApiResponse<AdjuntoResponseDto>>> create(@Valid @RequestBody CreateAdjuntoRequestDto dto) {
        return service.create(dto)
            .map(a -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Adjunto registrado", false, a)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar adjunto (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Adjunto eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar adjunto por id")
    public Mono<ResponseEntity<ApiResponse<AdjuntoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(a -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, a)));
    }

    @GetMapping
    @Operation(summary = "Listar adjuntos de una entidad (modulo, entidad, entidadId)")
    public Mono<ResponseEntity<ApiResponse<List<AdjuntoResponseDto>>>> listByObjeto(
            @RequestParam String modulo, @RequestParam String entidad, @RequestParam Long entidadId) {
        return service.listByObjeto(modulo, entidad, entidadId)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }
}
