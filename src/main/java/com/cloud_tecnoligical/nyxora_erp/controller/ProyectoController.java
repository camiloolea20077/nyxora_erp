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
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateProyectoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.ProyectoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.ProyectoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateProyectoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ProyectoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/proyectos")
@Tag(name = "Proyectos", description = "Proyectos (Común)")
public class ProyectoController {

    private final ProyectoService service;

    public ProyectoController(ProyectoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear proyecto")
    public Mono<ResponseEntity<ApiResponse<ProyectoResponseDto>>> create(@Valid @RequestBody CreateProyectoRequestDto dto) {
        return service.create(dto)
            .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Proyecto creado", false, p)));
    }

    @PutMapping
    @Operation(summary = "Actualizar proyecto")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateProyectoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Proyecto actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proyecto (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Proyecto eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar proyecto por id")
    public Mono<ResponseEntity<ApiResponse<ProyectoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar proyectos (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ProyectoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
