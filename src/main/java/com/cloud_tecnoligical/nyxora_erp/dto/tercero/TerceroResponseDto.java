package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerceroResponseDto {
    private Long id;
    private Long tipoIdentificacionId;
    private String numeroDocumento;
    private Short digitoVerificacion;
    private String tipoPersona;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String razonSocial;
    private String nombreComercial;
    private String nombreRepresentanteLegal;
    private String documentoRepresentanteLegal;
    private String nombre;
    private Long generoId;
    private Long estadoCivilId;
    private LocalDate fechaNacimiento;
    private Long municipioId;
    private Long barrioId;
    private String direccion;
    private String sitioWeb;
    private LocalDate fechaExpedicionDocumento;
    private Long municipioExpedicionId;
    private LocalDate fechaVencimientoDocumento;
    private Long actividadEconomicaId;
    private Long tipoContribuyenteId;
    private Boolean responsableIva;
    private Boolean esAutoretenedorIva;
    private Boolean esAutoretenedorIca;
    private Boolean esAutoretenedorFuente;
    private Boolean declarante;
    private Boolean aplicaArt383;
    private Boolean tieneRut;
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
    private Boolean active;
    private LocalDateTime createdAt;
    private List<Long> tipoTerceroIds;
}
