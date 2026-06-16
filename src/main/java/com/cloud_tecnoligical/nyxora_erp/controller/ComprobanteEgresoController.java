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
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ComprobanteEgresoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ComprobanteEgresoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateComprobanteEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.GirarEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.UpdateComprobanteEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ComprobanteEgresoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/egresos")
@Tag(name = "Egresos", description = "Comprobantes de egreso: pago a terceros y obligaciones (Tesorería)")
public class ComprobanteEgresoController {

    private final ComprobanteEgresoService service;

    public ComprobanteEgresoController(ComprobanteEgresoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear egreso (borrador; opcional aplicar a obligación)")
    public Mono<ResponseEntity<ApiResponse<ComprobanteEgresoResponseDto>>> create(@Valid @RequestBody CreateComprobanteEgresoRequestDto dto) {
        return service.create(dto)
            .map(e -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Egreso creado", false, e)));
    }

    @PutMapping
    @Operation(summary = "Actualizar egreso (solo borrador)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateComprobanteEgresoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Egreso actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar egreso (lógico, solo borrador)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Egreso eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar egreso por id")
    public Mono<ResponseEntity<ApiResponse<ComprobanteEgresoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(e -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, e)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar egresos (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ComprobanteEgresoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/girar")
    @Operation(summary = "Girar egreso (borrador → girado; aplica a obligación y opcional asiento)")
    public Mono<ResponseEntity<ApiResponse<ComprobanteEgresoResponseDto>>> girar(
            @PathVariable Long id, @RequestBody(required = false) GirarEgresoRequestDto params) {
        return service.girar(id, params != null ? params : new GirarEgresoRequestDto())
            .map(e -> ResponseEntity.ok(new ApiResponse<>(200, "Egreso girado", false, e)));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular egreso (reversa la aplicación a la obligación si estaba girado)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> anular(@PathVariable Long id) {
        return service.anular(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Egreso anulado", false, ok)));
    }
}
