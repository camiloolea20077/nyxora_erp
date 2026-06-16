package com.cloud_tecnoligical.nyxora_erp.mapper.presupuesto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateRubroPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.RubroPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.RubroPresupuestalEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RubroPresupuestalMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "izquierda", ignore = true),
        @Mapping(target = "derecha", ignore = true),
        @Mapping(target = "nivel", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "vigenciaId", target = "vigencia_id"),
        @Mapping(source = "rubroPadreId", target = "rubro_padre_id"),
        @Mapping(source = "tipoRubro", target = "tipo_rubro"),
        @Mapping(source = "codigoRubro", target = "codigo_rubro"),
        @Mapping(source = "nombreRubro", target = "nombre_rubro"),
        @Mapping(source = "manejaMovimiento", target = "maneja_movimiento"),
        @Mapping(source = "homologacionCircularUnica", target = "homologacion_circular_unica")
    })
    RubroPresupuestalEntity toEntity(CreateRubroPresupuestalRequestDto dto);

    @Mappings({
        @Mapping(source = "vigencia_id", target = "vigenciaId"),
        @Mapping(source = "rubro_padre_id", target = "rubroPadreId"),
        @Mapping(source = "tipo_rubro", target = "tipoRubro"),
        @Mapping(source = "codigo_rubro", target = "codigoRubro"),
        @Mapping(source = "nombre_rubro", target = "nombreRubro"),
        @Mapping(source = "maneja_movimiento", target = "manejaMovimiento"),
        @Mapping(source = "homologacion_circular_unica", target = "homologacionCircularUnica"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    RubroPresupuestalResponseDto toResponseDto(RubroPresupuestalEntity entity);
}
