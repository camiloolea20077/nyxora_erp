package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Catálogo de productos (R2DBC, ← com_productos, subconjunto comercial/logístico).
 * La columna jsonb 'imagen' se omite aquí; los proveedores/variantes/impuestos van en satélites.
 */
@Table("producto")
@Getter
@Setter
public class ProductoEntity {

    @Id
    private Long id;

    @Column("empresa_id")              private Long empresa_id;
    @Column("categoria_id")            private Long categoria_id;
    @Column("codigo")                  private String codigo;
    @Column("codigo_unspsc")           private String codigo_unspsc;
    @Column("nombre")                  private String nombre;
    @Column("descripcion")             private String descripcion;
    @Column("tipo")                    private String tipo;            // bien | servicio
    @Column("es_compuesto")            private Boolean es_compuesto;
    // Unidades y contenido
    @Column("unidad_mayor_id")         private Long unidad_mayor_id;
    @Column("unidad_menor_id")         private Long unidad_menor_id;
    @Column("contenido")               private BigDecimal contenido;
    // Inventario / logística
    @Column("maneja_inventario")       private Boolean maneja_inventario;
    @Column("maneja_lote")             private Boolean maneja_lote;
    @Column("maneja_desperdicio")      private Boolean maneja_desperdicio;
    @Column("es_devolutivo")           private Boolean es_devolutivo;
    @Column("stock_minimo")            private BigDecimal stock_minimo;
    @Column("stock_maximo")            private BigDecimal stock_maximo;
    @Column("tiempo_reabastecimiento") private Integer tiempo_reabastecimiento;
    // Comercial / tributario
    @Column("impuesto_id")             private Long impuesto_id;
    @Column("discrimina_iva")          private Boolean discrimina_iva;
    @Column("aplica_impuesto_bolsa")   private Boolean aplica_impuesto_bolsa;
    @Column("tarifa_maxima")           private BigDecimal tarifa_maxima;
    @Column("es_pos")                  private Boolean es_pos;
    @Column("recurso_id")              private Long recurso_id;
    @Column("activo")                  private Boolean activo;
    // Auditoría
    @Column("created_at")              private LocalDateTime created_at;
    @Column("updated_at")              private LocalDateTime updated_at;
    @Column("deleted_at")              private LocalDateTime deleted_at;
    @Column("usuario_creacion")        private Long usuario_creacion;
    @Column("usuario_modificacion")    private Long usuario_modificacion;
}
