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

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateGrupoAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GrupoAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GrupoAcademicoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateGrupoAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.GrupoAcademicoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/grupos-academicos")
@Tag(name = "Grupos académicos", description = "Catálogo de grupos académicos")
public class GrupoAcademicoController {

    private final GrupoAcademicoService service;

    public GrupoAcademicoController(GrupoAcademicoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear grupo académico")
    public Mono<ResponseEntity<ApiResponse<GrupoAcademicoResponseDto>>> create(@Valid @RequestBody CreateGrupoAcademicoRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Grupo creado", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar grupo académico")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateGrupoAcademicoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Grupo actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar grupo académico (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Grupo eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar grupo por id")
    public Mono<ResponseEntity<ApiResponse<GrupoAcademicoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar grupos (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<GrupoAcademicoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
