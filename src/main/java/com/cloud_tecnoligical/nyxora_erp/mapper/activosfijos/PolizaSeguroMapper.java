package com.cloud_tecnoligical.nyxora_erp.mapper.activosfijos;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreatePolizaSeguroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.PolizaSeguroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.PolizaSeguroEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PolizaSeguroMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "aseguradoraId", target = "aseguradora_id"),
        @Mapping(source = "fechaInicio", target = "fecha_inicio"),
        @Mapping(source = "fechaFin", target = "fecha_fin"),
        @Mapping(source = "valorAsegurado", target = "valor_asegurado")
    })
    PolizaSeguroEntity toEntity(CreatePolizaSeguroRequestDto dto);

    @Mappings({
        @Mapping(source = "aseguradora_id", target = "aseguradoraId"),
        @Mapping(target = "aseguradoraNombre", ignore = true),
        @Mapping(source = "fecha_inicio", target = "fechaInicio"),
        @Mapping(source = "fecha_fin", target = "fechaFin"),
        @Mapping(source = "valor_asegurado", target = "valorAsegurado"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    PolizaSeguroResponseDto toResponseDto(PolizaSeguroEntity entity);
}
