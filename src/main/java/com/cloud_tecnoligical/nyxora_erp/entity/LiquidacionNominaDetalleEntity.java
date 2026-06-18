package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Detalle de la liquidación de nómina (R2DBC, APPEND-ONLY: sin update/delete). */
@Table("liquidacion_nomina_detalle")
@Getter
@Setter
public class LiquidacionNominaDetalleEntity {

    @Id
    private Long id;

    @Column("empresa_id")            private Long empresa_id;
    @Column("liquidacion_nomina_id") private Long liquidacion_nomina_id;
    @Column("empleado_id")           private Long empleado_id;
    @Column("vinculacion_id")        private Long vinculacion_id;
    @Column("concepto_nomina_id")    private Long concepto_nomina_id;
    @Column("centro_costo_id")       private Long centro_costo_id;
    @Column("fecha_liquidacion")     private LocalDateTime fecha_liquidacion;
    @Column("fecha_inicial")         private LocalDate fecha_inicial;
    @Column("fecha_final")           private LocalDate fecha_final;
    @Column("base")                  private BigDecimal base;
    @Column("porcentaje")            private BigDecimal porcentaje;
    @Column("cantidad")              private BigDecimal cantidad;
    @Column("valor")                 private BigDecimal valor;
    @Column("valor_empleado")        private BigDecimal valor_empleado;
    @Column("valor_patrono")         private BigDecimal valor_patrono;
    @Column("valor_entidad")         private BigDecimal valor_entidad;
    @Column("tipo_aporte")           private String tipo_aporte;
    @Column("created_at")            private LocalDateTime created_at;
    @Column("usuario_creacion")      private Long usuario_creacion;
}
