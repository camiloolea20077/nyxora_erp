package com.cloud_tecnoligical.nyxora_erp.mapper.inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateBodegaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateBodegaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.BodegaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BodegaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "sedeId", target = "sede_id"),
        @Mapping(source = "centroCostoId", target = "centro_costo_id"),
        @Mapping(source = "tipoAbastecimiento", target = "tipo_abastecimiento"),
        @Mapping(source = "permiteCompra", target = "permite_compra")
    })
    BodegaEntity toEntity(CreateBodegaRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "sedeId", target = "sede_id"),
        @Mapping(source = "centroCostoId", target = "centro_costo_id"),
        @Mapping(source = "tipoAbastecimiento", target = "tipo_abastecimiento"),
        @Mapping(source = "permiteCompra", target = "permite_compra")
    })
    void updateEntityFromDto(UpdateBodegaRequestDto dto, @MappingTarget BodegaEntity entity);

    @Mappings({
        @Mapping(source = "sede_id", target = "sedeId"),
        @Mapping(source = "centro_costo_id", target = "centroCostoId"),
        @Mapping(source = "tipo_abastecimiento", target = "tipoAbastecimiento"),
        @Mapping(source = "permite_compra", target = "permiteCompra"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    BodegaResponseDto toResponseDto(BodegaEntity entity);
}
