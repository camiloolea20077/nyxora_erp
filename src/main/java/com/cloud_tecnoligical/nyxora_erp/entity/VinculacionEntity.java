package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Vinculación del empleado (R2DBC). Empleado = tercero. */
@Table("vinculacion")
@Getter
@Setter
public class VinculacionEntity {

    @Id
    private Long id;

    @Column("empresa_id")               private Long empresa_id;
    @Column("empleado_id")              private Long empleado_id;
    @Column("cargo_id")                 private Long cargo_id;
    @Column("grupo_nomina_id")          private Long grupo_nomina_id;
    @Column("codigo")                   private String codigo;
    @Column("fecha")                    private LocalDate fecha;
    @Column("fecha_fin")                private LocalDate fecha_fin;
    @Column("tipo_vinculacion")         private String tipo_vinculacion;
    @Column("tipo_contrato")            private String tipo_contrato;
    @Column("sueldo")                   private BigDecimal sueldo;
    @Column("hora_trabajo")             private Integer hora_trabajo;
    @Column("periodo_prueba")           private Boolean periodo_prueba;
    @Column("fecha_fin_periodo_prueba") private LocalDate fecha_fin_periodo_prueba;
    @Column("frecuencia_pago")          private String frecuencia_pago;
    @Column("jefe_id")                  private Long jefe_id;
    @Column("sede_id")                  private Long sede_id;
    @Column("dependencia_id")           private Long dependencia_id;
    @Column("municipio_vinculacion_id") private Long municipio_vinculacion_id;
    @Column("tipo_cotizante")           private String tipo_cotizante;
    @Column("estado_vinculacion")       private String estado_vinculacion;
    @Column("objeto")                   private String objeto;
    @Column("temporal")                 private Boolean temporal;
    @Column("activo")                   private Boolean activo;
    @Column("created_at")               private LocalDateTime created_at;
    @Column("updated_at")               private LocalDateTime updated_at;
    @Column("deleted_at")               private LocalDateTime deleted_at;
    @Column("usuario_creacion")         private Long usuario_creacion;
    @Column("usuario_modificacion")     private Long usuario_modificacion;
}
