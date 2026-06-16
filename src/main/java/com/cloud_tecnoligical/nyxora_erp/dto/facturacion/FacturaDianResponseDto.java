package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaDianResponseDto {
    private Long id;
    private Long facturaId;
    private String cufe;
    private String estadoDian;
    private LocalDate fechaAcuse;
    private String comentarioAcuse;
    private LocalDateTime createdAt;
}
