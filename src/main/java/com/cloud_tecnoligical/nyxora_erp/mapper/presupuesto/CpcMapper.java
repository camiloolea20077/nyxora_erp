package com.cloud_tecnoligical.nyxora_erp.mapper.presupuesto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CpcResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateCpcRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CpcEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CpcMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "vigenciaId", target = "vigencia_id"),
        @Mapping(source = "cpcPadreId", target = "cpc_padre_id"),
        @Mapping(source = "manejaMovimiento", target = "maneja_movimiento")
    })
    CpcEntity toEntity(CreateCpcRequestDto dto);

    @Mappings({
        @Mapping(source = "vigencia_id", target = "vigenciaId"),
        @Mapping(source = "cpc_padre_id", target = "cpcPadreId"),
        @Mapping(source = "maneja_movimiento", target = "manejaMovimiento"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    CpcResponseDto toResponseDto(CpcEntity entity);
}
