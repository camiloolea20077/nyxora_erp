package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Aporte a seguridad social (PILA) por empleado/liquidación (R2DBC, APPEND-ONLY). */
@Table("aporte_pila")
@Getter
@Setter
public class AportePilaEntity {

    @Id
    private Long id;

    @Column("empresa_id")            private Long empresa_id;
    @Column("liquidacion_nomina_id") private Long liquidacion_nomina_id;
    @Column("empleado_id")           private Long empleado_id;
    @Column("tipo_aporte")           private String tipo_aporte;
    @Column("ibc")                   private BigDecimal ibc;
    @Column("valor_empleado")        private BigDecimal valor_empleado;
    @Column("valor_patrono")         private BigDecimal valor_patrono;
    @Column("created_at")            private LocalDateTime created_at;
}
