package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CreateAcuerdoPagoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.AcuerdoPagoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/acuerdos-pago")
@Tag(name = "Acuerdos de pago", description = "Cartera: acuerdos de pago sobre cuentas por cobrar (Cartera)")
public class AcuerdoPagoController {

    private final AcuerdoPagoService service;

    public AcuerdoPagoController(AcuerdoPagoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear acuerdo de pago (con cuotas); marca la CxC en_acuerdo")
    public Mono<ResponseEntity<ApiResponse<AcuerdoPagoResponseDto>>> create(@Valid @RequestBody CreateAcuerdoPagoRequestDto dto) {
        return service.create(dto)
            .map(a -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Acuerdo de pago creado", false, a)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar acuerdo de pago por id (con cuotas)")
    public Mono<ResponseEntity<ApiResponse<AcuerdoPagoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(a -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, a)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar acuerdos de pago (paginado)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<AcuerdoPagoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular acuerdo de pago (vigente → anulado; restaura la CxC a vigente)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> anular(@PathVariable Long id) {
        return service.anular(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Acuerdo anulado", false, ok)));
    }
}
