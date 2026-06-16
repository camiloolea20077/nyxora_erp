package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFacturaProveedorRequestDto {

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    private Long receptorId;
    private String numeroDocumento;
    private String cufe;
    private LocalDate fechaRecepcion;
    private BigDecimal valorFactura;
    private String emailRemitente;
    private String pdfUrl;
}
