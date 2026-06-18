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

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateProgramaAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.ProgramaAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.ProgramaAcademicoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateProgramaAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.ProgramaAcademicoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/programas-academicos")
@Tag(name = "Programas académicos", description = "Catálogo de programas académicos")
public class ProgramaAcademicoController {

    private final ProgramaAcademicoService service;

    public ProgramaAcademicoController(ProgramaAcademicoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear programa académico")
    public Mono<ResponseEntity<ApiResponse<ProgramaAcademicoResponseDto>>> create(@Valid @RequestBody CreateProgramaAcademicoRequestDto dto) {
        return service.create(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Programa creado", false, m)));
    }

    @PutMapping
    @Operation(summary = "Actualizar programa académico")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateProgramaAcademicoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Programa actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar programa académico (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Programa eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar programa por id")
    public Mono<ResponseEntity<ApiResponse<ProgramaAcademicoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar programas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ProgramaAcademicoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
