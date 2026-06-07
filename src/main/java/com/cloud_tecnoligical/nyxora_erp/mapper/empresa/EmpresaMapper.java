package com.cloud_tecnoligical.nyxora_erp.mapper.empresa;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.cloud_tecnoligical.nyxora_erp.dto.empresa.CreateEmpresaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.EmpresaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.UpdateEmpresaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EmpresaEntity;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "digitoVerificacion", target = "digito_verificacion"),
        @Mapping(source = "razonSocial", target = "razon_social"),
        @Mapping(source = "nombreComercial", target = "nombre_comercial"),
        @Mapping(source = "tipoPersona", target = "tipo_persona"),
        @Mapping(source = "representanteLegal", target = "representante_legal"),
        @Mapping(source = "regimenTributario", target = "regimen_tributario"),
        @Mapping(source = "tipoContribuyenteId", target = "tipo_contribuyente_id"),
        @Mapping(source = "responsabilidadFiscal", target = "responsabilidad_fiscal"),
        @Mapping(source = "actividadEconomicaId", target = "actividad_economica_id"),
        @Mapping(source = "sitioWeb", target = "sitio_web"),
        @Mapping(source = "municipioId", target = "municipio_id"),
        @Mapping(source = "codigoPostal", target = "codigo_postal"),
        @Mapping(source = "logoUrl", target = "logo_url")
    })
    EmpresaEntity toEntity(CreateEmpresaRequestDto dto);

    @Mappings({
        @Mapping(source = "digito_verificacion", target = "digitoVerificacion"),
        @Mapping(source = "razon_social", target = "razonSocial"),
        @Mapping(source = "nombre_comercial", target = "nombreComercial"),
        @Mapping(source = "tipo_persona", target = "tipoPersona"),
        @Mapping(source = "representante_legal", target = "representanteLegal"),
        @Mapping(source = "regimen_tributario", target = "regimenTributario"),
        @Mapping(source = "tipo_contribuyente_id", target = "tipoContribuyenteId"),
        @Mapping(source = "responsabilidad_fiscal", target = "responsabilidadFiscal"),
        @Mapping(source = "actividad_economica_id", target = "actividadEconomicaId"),
        @Mapping(source = "sitio_web", target = "sitioWeb"),
        @Mapping(source = "municipio_id", target = "municipioId"),
        @Mapping(source = "codigo_postal", target = "codigoPostal"),
        @Mapping(source = "logo_url", target = "logoUrl"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    EmpresaResponseDto toResponseDto(EmpresaEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "digitoVerificacion", target = "digito_verificacion"),
        @Mapping(source = "razonSocial", target = "razon_social"),
        @Mapping(source = "nombreComercial", target = "nombre_comercial"),
        @Mapping(source = "tipoPersona", target = "tipo_persona"),
        @Mapping(source = "representanteLegal", target = "representante_legal"),
        @Mapping(source = "regimenTributario", target = "regimen_tributario"),
        @Mapping(source = "tipoContribuyenteId", target = "tipo_contribuyente_id"),
        @Mapping(source = "responsabilidadFiscal", target = "responsabilidad_fiscal"),
        @Mapping(source = "actividadEconomicaId", target = "actividad_economica_id"),
        @Mapping(source = "sitioWeb", target = "sitio_web"),
        @Mapping(source = "municipioId", target = "municipio_id"),
        @Mapping(source = "codigoPostal", target = "codigo_postal"),
        @Mapping(source = "logoUrl", target = "logo_url")
        // 'active' del DTO se aplica manualmente en el service (entity.activo)
    })
    void updateEntityFromDto(UpdateEmpresaRequestDto dto, @MappingTarget EmpresaEntity entity);
}
