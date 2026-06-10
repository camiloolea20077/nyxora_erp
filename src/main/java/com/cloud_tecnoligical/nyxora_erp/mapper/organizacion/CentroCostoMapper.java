package com.cloud_tecnoligical.nyxora_erp.mapper.organizacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CentroCostoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateCentroCostoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateCentroCostoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CentroCostoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CentroCostoMapper {

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
        @Mapping(source = "centroCostoPadreId", target = "centro_costo_padre_id"),
        @Mapping(source = "tipoCentroCosto", target = "tipo_centro_costo"),
        @Mapping(source = "claseCentroCosto", target = "clase_centro_costo"),
        @Mapping(source = "esObservacion", target = "es_observacion"),
        @Mapping(source = "manejaPlanFinanciero", target = "maneja_plan_financiero"),
        @Mapping(source = "terceroId", target = "tercero_id"),
        @Mapping(source = "unidadNegocioId", target = "unidad_negocio_id")
    })
    CentroCostoEntity toEntity(CreateCentroCostoRequestDto dto);

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
        @Mapping(source = "centroCostoPadreId", target = "centro_costo_padre_id"),
        @Mapping(source = "tipoCentroCosto", target = "tipo_centro_costo"),
        @Mapping(source = "claseCentroCosto", target = "clase_centro_costo"),
        @Mapping(source = "esObservacion", target = "es_observacion"),
        @Mapping(source = "manejaPlanFinanciero", target = "maneja_plan_financiero"),
        @Mapping(source = "terceroId", target = "tercero_id"),
        @Mapping(source = "unidadNegocioId", target = "unidad_negocio_id")
    })
    void updateEntityFromDto(UpdateCentroCostoRequestDto dto, @MappingTarget CentroCostoEntity entity);

    @Mappings({
        @Mapping(source = "sede_id", target = "sedeId"),
        @Mapping(source = "centro_costo_padre_id", target = "centroCostoPadreId"),
        @Mapping(source = "tipo_centro_costo", target = "tipoCentroCosto"),
        @Mapping(source = "clase_centro_costo", target = "claseCentroCosto"),
        @Mapping(source = "es_observacion", target = "esObservacion"),
        @Mapping(source = "maneja_plan_financiero", target = "manejaPlanFinanciero"),
        @Mapping(source = "tercero_id", target = "terceroId"),
        @Mapping(source = "unidad_negocio_id", target = "unidadNegocioId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    CentroCostoResponseDto toResponseDto(CentroCostoEntity entity);
}
