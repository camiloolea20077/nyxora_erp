package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Activo fijo (R2DBC). */
@Table("activo_fijo")
@Getter
@Setter
public class ActivoFijoEntity {

    @Id
    private Long id;

    @Column("empresa_id")            private Long empresa_id;
    @Column("producto_id")           private Long producto_id;
    @Column("codigo")                private String codigo;
    @Column("codigo_unspsc")         private String codigo_unspsc;
    @Column("codigo_barra")          private String codigo_barra;
    @Column("nombre")                private String nombre;
    @Column("descripcion")           private String descripcion;
    @Column("marca_id")              private Long marca_id;
    @Column("unidad_mayor_id")       private Long unidad_mayor_id;
    @Column("numero_serie")          private String numero_serie;
    @Column("modelo")                private String modelo;
    @Column("bodega_id")             private Long bodega_id;
    @Column("centro_costo_id")       private Long centro_costo_id;
    @Column("proveedor_id")          private Long proveedor_id;
    @Column("numero_factura")        private String numero_factura;
    @Column("fecha_factura")         private LocalDate fecha_factura;
    @Column("valor_compra")          private BigDecimal valor_compra;
    @Column("valor_salvamento")      private BigDecimal valor_salvamento;
    @Column("porcentaje_salvamento") private BigDecimal porcentaje_salvamento;
    @Column("metodo_depreciacion")   private String metodo_depreciacion;
    @Column("tipo_depreciacion")     private String tipo_depreciacion;
    @Column("valor_depreciacion")    private BigDecimal valor_depreciacion;
    @Column("deterioro")             private BigDecimal deterioro;
    @Column("valor_actual")          private BigDecimal valor_actual;
    @Column("avaluo")                private BigDecimal avaluo;
    @Column("vida_util")             private Integer vida_util;
    @Column("meses_depreciados")     private Integer meses_depreciados;
    @Column("capitalizado")          private BigDecimal capitalizado;
    @Column("estado_activo")         private String estado_activo;
    @Column("fecha_salida_servicio") private LocalDate fecha_salida_servicio;
    @Column("activo")                private Boolean activo;
    @Column("created_at")            private LocalDateTime created_at;
    @Column("updated_at")            private LocalDateTime updated_at;
    @Column("deleted_at")            private LocalDateTime deleted_at;
    @Column("usuario_creacion")      private Long usuario_creacion;
    @Column("usuario_modificacion")  private Long usuario_modificacion;
}
