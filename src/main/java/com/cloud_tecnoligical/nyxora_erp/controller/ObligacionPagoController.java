package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.CreateObligacionPagoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoTableDto;
import com.cloud_tecnoligical.nyxora_erp.service.ObligacionPagoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/obligaciones-pago")
@Tag(name = "Obligaciones de pago", description = "Cuentas por pagar a proveedores con retenciones (Cuentas por pagar)")
public class ObligacionPagoController {

    private final ObligacionPagoService service;

    public ObligacionPagoController(ObligacionPagoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear obligación de pago (con retenciones; saldo = total − retenciones)")
    public Mono<ResponseEntity<ApiResponse<ObligacionPagoResponseDto>>> create(@Valid @RequestBody CreateObligacionPagoRequestDto dto) {
        return service.create(dto)
            .map(o -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Obligación de pago creada", false, o)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar obligación de pago por id (con retenciones)")
    public Mono<ResponseEntity<ApiResponse<ObligacionPagoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(o -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, o)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar obligaciones de pago (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ObligacionPagoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular obligación de pago (sin pagos aplicados)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> anular(@PathVariable Long id) {
        return service.anular(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Obligación anulada", false, ok)));
    }
}
