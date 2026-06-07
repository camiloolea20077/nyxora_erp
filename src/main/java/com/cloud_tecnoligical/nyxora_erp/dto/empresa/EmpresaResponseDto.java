package com.cloud_tecnoligical.nyxora_erp.dto.empresa;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaResponseDto {
    private Long id;
    private String nit;
    private Short digitoVerificacion;
    private String razonSocial;
    private String nombreComercial;
    private String codigo;
    private String tipoPersona;
    private String representanteLegal;
    private String regimenTributario;
    private Long tipoContribuyenteId;
    private String responsabilidadFiscal;
    private Long actividadEconomicaId;
    private String sector;
    private String email;
    private String telefono;
    private String celular;
    private String sitioWeb;
    private Long municipioId;
    private String direccion;
    private String codigoPostal;
    private String logoUrl;
    private Boolean active;
    private LocalDateTime createdAt;
}
