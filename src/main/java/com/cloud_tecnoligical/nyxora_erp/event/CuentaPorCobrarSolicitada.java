package com.cloud_tecnoligical.nyxora_erp.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;

/**
 * Evento: una factura emitida solicita registrar una cuenta por cobrar al cliente.
 * Lo consumirá la interfaz de Cartera (Sprint 8). Hoy se publica best-effort sin listener.
 */
@Getter
public class CuentaPorCobrarSolicitada implements DomainEvent {

    private final Long empresaId;
    private final Long usuarioId;
    private final Long sedeId;
    private final LocalDateTime ocurridoEn;

    private final Long facturaId;
    private final Long clienteId;
    private final String numeroFactura;
    private final LocalDate fecha;
    private final LocalDate fechaVencimiento;
    private final BigDecimal valor;

    public CuentaPorCobrarSolicitada(Long empresaId, Long usuarioId, Long sedeId,
                                     Long facturaId, Long clienteId, String numeroFactura,
                                     LocalDate fecha, LocalDate fechaVencimiento, BigDecimal valor) {
        this.empresaId = empresaId;
        this.usuarioId = usuarioId;
        this.sedeId = sedeId;
        this.ocurridoEn = LocalDateTime.now();
        this.facturaId = facturaId;
        this.clienteId = clienteId;
        this.numeroFactura = numeroFactura;
        this.fecha = fecha;
        this.fechaVencimiento = fechaVencimiento;
        this.valor = valor;
    }
}
