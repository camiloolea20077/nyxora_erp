package com.cloud_tecnoligical.nyxora_erp.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;

/**
 * Evento: una carga docente se traslada a la nómina del catedrático. La interfaz de nómina
 * (InterfazNominaDocenteListener) lo consume y registra una novedad_nomina por las horas dictadas.
 */
@Getter
public class CargaDocenteRegistrada implements DomainEvent {

    private final Long empresaId;
    private final Long usuarioId;
    private final Long sedeId;
    private final LocalDateTime ocurridoEn;

    private final Long vinculacionId;
    private final Long conceptoNominaId;
    private final BigDecimal cantidadValor;
    private final String descripcion;
    private final Long cargaAcademicaId;

    public CargaDocenteRegistrada(Long empresaId, Long usuarioId, Long sedeId,
                                  Long vinculacionId, Long conceptoNominaId, BigDecimal cantidadValor,
                                  String descripcion, Long cargaAcademicaId) {
        this.empresaId = empresaId;
        this.usuarioId = usuarioId;
        this.sedeId = sedeId;
        this.ocurridoEn = LocalDateTime.now();
        this.vinculacionId = vinculacionId;
        this.conceptoNominaId = conceptoNominaId;
        this.cantidadValor = cantidadValor;
        this.descripcion = descripcion;
        this.cargaAcademicaId = cargaAcademicaId;
    }
}
