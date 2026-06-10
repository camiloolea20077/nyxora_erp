package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateComprobanteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.event.AsientoContableSolicitado;
import com.cloud_tecnoligical.nyxora_erp.event.DomainEventBus;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

/**
 * Endpoint DEMO para probar el bus de eventos de dominio + interfaz contable de punta a punta,
 * sin tener aún compras/facturación. Publica un AsientoContableSolicitado; la interfaz contable
 * (listener) lo procesa de forma asíncrona y genera el comprobante.
 */
@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Eventos (demo)", description = "Publicación de eventos de dominio para probar la interfaz contable")
public class EventoDemoController {

    private final DomainEventBus bus;

    public EventoDemoController(DomainEventBus bus) {
        this.bus = bus;
    }

    @PostMapping("/asiento-demo")
    @Operation(summary = "Publica un AsientoContableSolicitado (procesado async por la interfaz contable)")
    public Mono<ResponseEntity<ApiResponse<String>>> publicarAsiento(@Valid @RequestBody CreateComprobanteRequestDto dto) {
        return TenantContext.get().map(t -> {
            AsientoContableSolicitado evento = new AsientoContableSolicitado(
                t.getEmpresaId(), t.getUsuarioId(), t.getSedeId(),
                dto.getPeriodoContableId(), dto.getFecha(), dto.getDescripcion(),
                dto.getOrigenModulo() != null ? dto.getOrigenModulo() : "demo",
                dto.getOrigenId(), dto.getMovimientos());
            bus.publish(evento);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>(202, "Evento publicado; la interfaz contable lo procesará", false,
                    "AsientoContableSolicitado"));
        });
    }
}
