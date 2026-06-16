package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaProveedorResponseDto {
    private Long id;
    private Long proveedorId;
    private Long receptorId;
    private String numeroDocumento;
    private String cufe;
    private LocalDate fechaRecepcion;
    private BigDecimal valorFactura;
    private String emailRemitente;
    private String pdfUrl;
    private String estado;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<FacturaProveedorEventoResponseDto> eventos;
}
