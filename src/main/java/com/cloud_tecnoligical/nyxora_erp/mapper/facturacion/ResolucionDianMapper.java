package com.cloud_tecnoligical.nyxora_erp.mapper.facturacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.CreateResolucionDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.ResolucionDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ResolucionDianEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResolucionDianMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "consecutivo_actual", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "numeroResolucion", target = "numero_resolucion"),
        @Mapping(source = "facturaInicial", target = "factura_inicial"),
        @Mapping(source = "facturaFinal", target = "factura_final"),
        @Mapping(source = "fechaInicial", target = "fecha_inicial"),
        @Mapping(source = "fechaFinal", target = "fecha_final"),
        @Mapping(source = "claveTecnica", target = "clave_tecnica")
    })
    ResolucionDianEntity toEntity(CreateResolucionDianRequestDto dto);

    @Mappings({
        @Mapping(source = "numero_resolucion", target = "numeroResolucion"),
        @Mapping(source = "factura_inicial", target = "facturaInicial"),
        @Mapping(source = "factura_final", target = "facturaFinal"),
        @Mapping(source = "fecha_inicial", target = "fechaInicial"),
        @Mapping(source = "fecha_final", target = "fechaFinal"),
        @Mapping(source = "clave_tecnica", target = "claveTecnica"),
        @Mapping(source = "consecutivo_actual", target = "consecutivoActual"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ResolucionDianResponseDto toResponseDto(ResolucionDianEntity entity);
}
