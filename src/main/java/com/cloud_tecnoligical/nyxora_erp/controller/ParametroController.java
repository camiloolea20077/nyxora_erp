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
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.CreateParametroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.ParametroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.ParametroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.UpdateParametroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ParametroService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/parametros")
@Tag(name = "Parámetros", description = "Parámetros del sistema por empresa (Administración)")
public class ParametroController {

    private final ParametroService parametroService;

    public ParametroController(ParametroService parametroService) {
        this.parametroService = parametroService;
    }

    @PostMapping
    @Operation(summary = "Crear parámetro")
    public Mono<ResponseEntity<ApiResponse<ParametroResponseDto>>> create(@Valid @RequestBody CreateParametroRequestDto dto) {
        return parametroService.create(dto)
            .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Parámetro creado", false, p)));
    }

    @PutMapping
    @Operation(summary = "Actualizar parámetro (valor / tipo)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateParametroRequestDto dto) {
        return parametroService.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Parámetro actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar parámetro (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return parametroService.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Parámetro eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar parámetro por id")
    public Mono<ResponseEntity<ApiResponse<ParametroResponseDto>>> findById(@PathVariable Long id) {
        return parametroService.findById(id)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @GetMapping("/by-clave/{clave}")
    @Operation(summary = "Consultar parámetro por su clave")
    public Mono<ResponseEntity<ApiResponse<ParametroResponseDto>>> findByClave(@PathVariable String clave) {
        return parametroService.findByClave(clave)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar parámetros (paginado, búsqueda)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ParametroTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return parametroService.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
