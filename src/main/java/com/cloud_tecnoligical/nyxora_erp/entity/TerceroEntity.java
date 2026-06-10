package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Maestro de terceros (R2DBC). Subconjunto comercial/fiscal. Las columnas jsonb
 * (obligacion_dian, metadatos) se omiten aquí; los contactos/direcciones/cuentas van en satélites.
 */
@Table("tercero")
@Getter
@Setter
public class TerceroEntity {

    @Id
    private Long id;

    @Column("empresa_id")            private Long empresa_id;
    // Identificación
    @Column("tipo_identificacion_id") private Long tipo_identificacion_id;
    @Column("numero_documento")      private String numero_documento;
    @Column("digito_verificacion")   private Short digito_verificacion;
    @Column("tipo_persona")          private String tipo_persona;
    // Persona natural
    @Column("primer_nombre")         private String primer_nombre;
    @Column("segundo_nombre")        private String segundo_nombre;
    @Column("primer_apellido")       private String primer_apellido;
    @Column("segundo_apellido")      private String segundo_apellido;
    // Persona jurídica
    @Column("razon_social")          private String razon_social;
    @Column("nombre_comercial")      private String nombre_comercial;
    @Column("nombre_representante_legal")    private String nombre_representante_legal;
    @Column("documento_representante_legal") private String documento_representante_legal;
    // Nombre normalizado
    @Column("nombre")                private String nombre;
    // Personal
    @Column("genero_id")             private Long genero_id;
    @Column("estado_civil_id")       private Long estado_civil_id;
    @Column("fecha_nacimiento")      private LocalDate fecha_nacimiento;
    // Ubicación
    @Column("municipio_id")          private Long municipio_id;
    @Column("barrio_id")             private Long barrio_id;
    @Column("direccion")             private String direccion;
    @Column("sitio_web")             private String sitio_web;
    // Documento de identidad
    @Column("fecha_expedicion_documento")    private LocalDate fecha_expedicion_documento;
    @Column("municipio_expedicion_id")       private Long municipio_expedicion_id;
    @Column("fecha_vencimiento_documento")   private LocalDate fecha_vencimiento_documento;
    // Fiscal / DIAN
    @Column("actividad_economica_id") private Long actividad_economica_id;
    @Column("tipo_contribuyente_id")  private Long tipo_contribuyente_id;
    @Column("responsable_iva")        private Boolean responsable_iva;
    @Column("es_autoretenedor_iva")   private Boolean es_autoretenedor_iva;
    @Column("es_autoretenedor_ica")   private Boolean es_autoretenedor_ica;
    @Column("es_autoretenedor_fuente") private Boolean es_autoretenedor_fuente;
    @Column("declarante")             private Boolean declarante;
    @Column("aplica_art_383")         private Boolean aplica_art_383;
    @Column("tiene_rut")              private Boolean tiene_rut;
    // Comercial
    @Column("condicion_pago_cliente_id")   private Long condicion_pago_cliente_id;
    @Column("condicion_pago_proveedor_id") private Long condicion_pago_proveedor_id;
    @Column("forma_pago_cliente_id")       private Long forma_pago_cliente_id;
    @Column("forma_pago_proveedor_id")     private Long forma_pago_proveedor_id;
    @Column("interes_efectivo_mensual")    private BigDecimal interes_efectivo_mensual;
    @Column("cuenta_contable_proveedor_id") private Long cuenta_contable_proveedor_id;
    @Column("recurso_id")             private Long recurso_id;
    @Column("es_reciproco")           private Boolean es_reciproco;
    @Column("codigo_reciproco")       private String codigo_reciproco;
    // Otros
    @Column("observaciones")          private String observaciones;
    @Column("activo")                 private Boolean activo;
    // Auditoría
    @Column("created_at")             private LocalDateTime created_at;
    @Column("updated_at")             private LocalDateTime updated_at;
    @Column("deleted_at")             private LocalDateTime deleted_at;
    @Column("usuario_creacion")       private Long usuario_creacion;
    @Column("usuario_modificacion")   private Long usuario_modificacion;
}
