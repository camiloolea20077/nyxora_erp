package com.cloud_tecnoligical.nyxora_erp.mapper.cuentaspagar;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.CreateFacturaProveedorRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FacturaProveedorEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FacturaProveedorMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "xml_factura", ignore = true),
        @Mapping(target = "estado", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "proveedorId", target = "proveedor_id"),
        @Mapping(source = "receptorId", target = "receptor_id"),
        @Mapping(source = "numeroDocumento", target = "numero_documento"),
        @Mapping(source = "fechaRecepcion", target = "fecha_recepcion"),
        @Mapping(source = "valorFactura", target = "valor_factura"),
        @Mapping(source = "emailRemitente", target = "email_remitente"),
        @Mapping(source = "pdfUrl", target = "pdf_url")
    })
    FacturaProveedorEntity toEntity(CreateFacturaProveedorRequestDto dto);

    @Mappings({
        @Mapping(source = "proveedor_id", target = "proveedorId"),
        @Mapping(source = "receptor_id", target = "receptorId"),
        @Mapping(source = "numero_documento", target = "numeroDocumento"),
        @Mapping(source = "fecha_recepcion", target = "fechaRecepcion"),
        @Mapping(source = "valor_factura", target = "valorFactura"),
        @Mapping(source = "email_remitente", target = "emailRemitente"),
        @Mapping(source = "pdf_url", target = "pdfUrl"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "eventos", ignore = true)
    })
    FacturaProveedorResponseDto toResponseDto(FacturaProveedorEntity entity);
}
