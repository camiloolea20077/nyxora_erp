package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateReciboCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.ReciboCajaService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/recibos-caja")
@Tag(name = "Recibos de caja", description = "Recaudo: recibo + medios de pago + aplicación a cartera (Caja)")
public class ReciboCajaController {

    private final ReciboCajaService service;

    public ReciboCajaController(ReciboCajaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear recibo de caja (medios de pago + aplicación a CxC, opcional asiento)")
    public Mono<ResponseEntity<ApiResponse<ReciboCajaResponseDto>>> create(@Valid @RequestBody CreateReciboCajaRequestDto dto) {
        return service.create(dto)
            .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Recibo de caja creado", false, r)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar recibo de caja por id (con pagos y aplicaciones)")
    public Mono<ResponseEntity<ApiResponse<ReciboCajaResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(r -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, r)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar recibos de caja (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ReciboCajaTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular recibo de caja (registrado → anulado; reversa la aplicación a cartera)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> anular(@PathVariable Long id) {
        return service.anular(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Recibo anulado", false, ok)));
    }
}
