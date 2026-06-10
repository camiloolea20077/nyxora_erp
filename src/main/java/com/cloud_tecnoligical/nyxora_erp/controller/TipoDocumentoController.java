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
import com.cloud_tecnoligical.nyxora_erp.dto.documento.ConsecutivoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.ConsecutivoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.CreateTipoDocumentoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.TipoDocumentoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.TipoDocumentoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.UpdateTipoDocumentoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ConsecutivoService;
import com.cloud_tecnoligical.nyxora_erp.service.TipoDocumentoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tipos-documento")
@Tag(name = "Tipos de documento", description = "Motor de documentos: tipos + consecutivo atómico (Común)")
public class TipoDocumentoController {

    private final TipoDocumentoService service;
    private final ConsecutivoService consecutivoService;

    public TipoDocumentoController(TipoDocumentoService service, ConsecutivoService consecutivoService) {
        this.service = service;
        this.consecutivoService = consecutivoService;
    }

    @PostMapping
    @Operation(summary = "Crear tipo de documento")
    public Mono<ResponseEntity<ApiResponse<TipoDocumentoResponseDto>>> create(@Valid @RequestBody CreateTipoDocumentoRequestDto dto) {
        return service.create(dto)
            .map(td -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Tipo de documento creado", false, td)));
    }

    @PutMapping
    @Operation(summary = "Actualizar tipo de documento")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateTipoDocumentoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Tipo de documento actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de documento (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Tipo de documento eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar tipo de documento por id")
    public Mono<ResponseEntity<ApiResponse<TipoDocumentoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(td -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, td)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar tipos de documento (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<TipoDocumentoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/consecutivo")
    @Operation(summary = "Obtener el siguiente consecutivo (atómico) por tipo/sede/vigencia")
    public Mono<ResponseEntity<ApiResponse<ConsecutivoResponseDto>>> siguienteConsecutivo(
            @PathVariable Long id, @Valid @RequestBody ConsecutivoRequestDto request) {
        return consecutivoService.siguiente(id, request)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }
}
