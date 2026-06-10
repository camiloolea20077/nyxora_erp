package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTerceroRequestDto {

    // Identificación
    @NotNull(message = "El tipo de identificación es obligatorio")
    private Long tipoIdentificacionId;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20)
    private String numeroDocumento;

    private Short digitoVerificacion;

    @NotBlank(message = "El tipo de persona es obligatorio")
    @Pattern(regexp = "natural|juridica", message = "El tipo de persona debe ser 'natural' o 'juridica'")
    private String tipoPersona;

    // Persona natural
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    // Persona jurídica
    private String razonSocial;
    private String nombreComercial;
    private String nombreRepresentanteLegal;
    private String documentoRepresentanteLegal;

    // Personal
    private Long generoId;
    private Long estadoCivilId;
    private LocalDate fechaNacimiento;

    // Ubicación
    private Long municipioId;
    private Long barrioId;
    private String direccion;
    private String sitioWeb;

    // Documento
    private LocalDate fechaExpedicionDocumento;
    private Long municipioExpedicionId;
    private LocalDate fechaVencimientoDocumento;

    // Fiscal / DIAN
    private Long actividadEconomicaId;
    private Long tipoContribuyenteId;
    private Boolean responsableIva;
    private Boolean esAutoretenedorIva;
    private Boolean esAutoretenedorIca;
    private Boolean esAutoretenedorFuente;
    private Boolean declarante;
    private Boolean aplicaArt383;
    private Boolean tieneRut;

    // Comercial
    private Long condicionPagoClienteId;
    private Long condicionPagoProveedorId;
    private Long formaPagoClienteId;
    private Long formaPagoProveedorId;
    private BigDecimal interesEfectivoMensual;
    private Long cuentaContableProveedorId;
    private Long recursoId;
    private Boolean esReciproco;
    private String codigoReciproco;

    private String observaciones;

    /** Clasificación del tercero (cliente/proveedor/empleado...) por ids de tipo_tercero. */
    private List<Long> tipoTerceroIds;
}
