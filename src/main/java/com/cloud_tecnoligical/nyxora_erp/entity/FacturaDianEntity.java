package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Metadata de la factura electrónica (R2DBC). CUFE y estado del acuse DIAN. */
@Table("factura_dian")
@Getter
@Setter
public class FacturaDianEntity {

    @Id
    private Long id;

    @Column("factura_id")       private Long factura_id;
    @Column("cufe")             private String cufe;
    @Column("estado_dian")      private String estado_dian;     // enviada | aceptada | rechazada
    @Column("fecha_acuse")      private LocalDate fecha_acuse;
    @Column("comentario_acuse") private String comentario_acuse;
    @Column("created_at")       private LocalDateTime created_at;
    @Column("updated_at")       private LocalDateTime updated_at;
    @Column("deleted_at")       private LocalDateTime deleted_at;
}
