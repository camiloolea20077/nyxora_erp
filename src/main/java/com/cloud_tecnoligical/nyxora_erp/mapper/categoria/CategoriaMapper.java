package com.cloud_tecnoligical.nyxora_erp.mapper.categoria;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.categoria.CategoriaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.categoria.CreateCategoriaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.categoria.UpdateCategoriaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CategoriaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoriaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "categoriaPadreId", target = "categoria_padre_id"),
        @Mapping(source = "tipoProducto", target = "tipo_producto"),
        @Mapping(source = "metodoCosteo", target = "metodo_costeo")
    })
    CategoriaEntity toEntity(CreateCategoriaRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "categoriaPadreId", target = "categoria_padre_id"),
        @Mapping(source = "tipoProducto", target = "tipo_producto"),
        @Mapping(source = "metodoCosteo", target = "metodo_costeo")
    })
    void updateEntityFromDto(UpdateCategoriaRequestDto dto, @MappingTarget CategoriaEntity entity);

    @Mappings({
        @Mapping(source = "categoria_padre_id", target = "categoriaPadreId"),
        @Mapping(source = "tipo_producto", target = "tipoProducto"),
        @Mapping(source = "metodo_costeo", target = "metodoCosteo"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    CategoriaResponseDto toResponseDto(CategoriaEntity entity);
}
