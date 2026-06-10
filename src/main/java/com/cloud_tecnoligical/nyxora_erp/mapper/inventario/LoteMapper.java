package com.cloud_tecnoligical.nyxora_erp.mapper.inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateLoteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.LoteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateLoteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.LoteEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoteMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "productoVarianteId", target = "producto_variante_id"),
        @Mapping(source = "fechaFabricado", target = "fecha_fabricado"),
        @Mapping(source = "fechaVencimiento", target = "fecha_vencimiento")
    })
    LoteEntity toEntity(CreateLoteRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "productoVarianteId", target = "producto_variante_id"),
        @Mapping(source = "fechaFabricado", target = "fecha_fabricado"),
        @Mapping(source = "fechaVencimiento", target = "fecha_vencimiento")
    })
    void updateEntityFromDto(UpdateLoteRequestDto dto, @MappingTarget LoteEntity entity);

    @Mappings({
        @Mapping(source = "producto_variante_id", target = "productoVarianteId"),
        @Mapping(source = "fecha_fabricado", target = "fechaFabricado"),
        @Mapping(source = "fecha_vencimiento", target = "fechaVencimiento"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    LoteResponseDto toResponseDto(LoteEntity entity);
}
