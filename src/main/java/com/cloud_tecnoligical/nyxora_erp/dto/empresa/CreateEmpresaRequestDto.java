package com.cloud_tecnoligical.nyxora_erp.dto.empresa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmpresaRequestDto {

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 20, message = "El NIT no puede superar 20 caracteres")
    private String nit;

    private Short digitoVerificacion;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 255, message = "La razón social no puede superar 255 caracteres")
    private String razonSocial;

    private String nombreComercial;
    private String codigo;

    @NotBlank(message = "El tipo de persona es obligatorio")
    @Pattern(regexp = "natural|juridica", message = "El tipo de persona debe ser 'natural' o 'juridica'")
    private String tipoPersona;

    private String representanteLegal;
    private String regimenTributario;
    private Long tipoContribuyenteId;
    private String responsabilidadFiscal;
    private Long actividadEconomicaId;
    private String sector;

    @Email(message = "El email no es válido")
    private String email;

    private String telefono;
    private String celular;
    private String sitioWeb;
    private Long municipioId;
    private String direccion;
    private String codigoPostal;
    private String logoUrl;
}
