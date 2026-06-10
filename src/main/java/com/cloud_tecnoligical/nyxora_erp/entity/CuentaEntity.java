package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Cuenta del plan contable (R2DBC, ← con_plan_contable). Jerárquica (nested set). */
@Table("cuenta")
@Getter
@Setter
public class CuentaEntity {

    @Id
    private Long id;

    @Column("empresa_id")               private Long empresa_id;
    @Column("cuenta_padre_id")          private Long cuenta_padre_id;
    @Column("codigo_cuenta")            private String codigo_cuenta;
    @Column("nombre_cuenta")            private String nombre_cuenta;
    @Column("nivel")                    private Integer nivel;
    @Column("izquierda")                private Integer izquierda;
    @Column("derecha")                  private Integer derecha;
    @Column("naturaleza")               private String naturaleza;        // debito | credito
    @Column("tipo_cuenta")              private String tipo_cuenta;
    @Column("maneja_movimiento")        private Boolean maneja_movimiento;
    @Column("maneja_movimiento_manual") private Boolean maneja_movimiento_manual;
    @Column("maneja_tercero")           private Boolean maneja_tercero;
    @Column("maneja_centro_costo")      private Boolean maneja_centro_costo;
    @Column("maneja_impuesto")          private Boolean maneja_impuesto;
    @Column("maneja_proyecto")          private Boolean maneja_proyecto;
    @Column("maneja_recurso")           private Boolean maneja_recurso;
    @Column("maneja_saldo_contrario")   private Boolean maneja_saldo_contrario;
    @Column("es_corriente")             private Boolean es_corriente;
    @Column("activo")                   private Boolean activo;
    @Column("created_at")               private LocalDateTime created_at;
    @Column("updated_at")               private LocalDateTime updated_at;
    @Column("deleted_at")               private LocalDateTime deleted_at;
    @Column("usuario_creacion")         private Long usuario_creacion;
    @Column("usuario_modificacion")     private Long usuario_modificacion;
}
