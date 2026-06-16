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

import com.cloud_tecnoligical.nyxora_erp.dto.caja.AbrirCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CajaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CajaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.UpdateCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.CajaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cajas")
@Tag(name = "Cajas", description = "Puntos de recaudo: caja con apertura/cierre (Caja)")
public class CajaController {

    private final CajaService service;

    public CajaController(CajaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear caja")
    public Mono<ResponseEntity<ApiResponse<CajaResponseDto>>> create(@Valid @RequestBody CreateCajaRequestDto dto) {
        return service.create(dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Caja creada", false, c)));
    }

    @PutMapping
    @Operation(summary = "Actualizar caja")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateCajaRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Caja actualizada", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar caja (lógico, solo cerrada)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Caja eliminada", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar caja por id")
    public Mono<ResponseEntity<ApiResponse<CajaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, c)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar cajas (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<CajaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/abrir")
    @Operation(summary = "Abrir caja (cerrada → abierta, con saldo inicial)")
    public Mono<ResponseEntity<ApiResponse<CajaResponseDto>>> abrir(
            @PathVariable Long id, @RequestBody(required = false) AbrirCajaRequestDto dto) {
        return service.abrir(id, dto != null ? dto : new AbrirCajaRequestDto())
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "Caja abierta", false, c)));
    }

    @PostMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar caja (abierta → cerrada)")
    public Mono<ResponseEntity<ApiResponse<CajaResponseDto>>> cerrar(@PathVariable Long id) {
        return service.cerrar(id)
            .map(c -> ResponseEntity.ok(new ApiResponse<>(200, "Caja cerrada", false, c)));
    }
}
