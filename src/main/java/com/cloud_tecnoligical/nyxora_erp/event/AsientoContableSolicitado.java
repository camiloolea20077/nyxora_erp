package com.cloud_tecnoligical.nyxora_erp.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateMovimientoContableDto;

import lombok.Getter;

/**
 * Evento: un módulo (compras, facturación, caja…) solicita generar un asiento contable.
 * La interfaz contable (InterfazContableListener) lo consume y crea+confirma un comprobante.
 */
@Getter
public class AsientoContableSolicitado implements DomainEvent {

    private final Long empresaId;
    private final Long usuarioId;
    private final Long sedeId;
    private final LocalDateTime ocurridoEn;

    private final Long periodoContableId;
    private final LocalDate fecha;
    private final String descripcion;
    private final String origenModulo;
    private final Long origenId;
    private final List<CreateMovimientoContableDto> movimientos;

    public AsientoContableSolicitado(Long empresaId, Long usuarioId, Long sedeId,
                                     Long periodoContableId, LocalDate fecha, String descripcion,
                                     String origenModulo, Long origenId,
                                     List<CreateMovimientoContableDto> movimientos) {
        this.empresaId = empresaId;
        this.usuarioId = usuarioId;
        this.sedeId = sedeId;
        this.ocurridoEn = LocalDateTime.now();
        this.periodoContableId = periodoContableId;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.origenModulo = origenModulo;
        this.origenId = origenId;
        this.movimientos = movimientos;
    }
}
