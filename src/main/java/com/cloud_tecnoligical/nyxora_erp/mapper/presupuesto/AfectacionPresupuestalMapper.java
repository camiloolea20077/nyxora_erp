package com.cloud_tecnoligical.nyxora_erp.mapper.presupuesto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.AfectacionPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.AfectacionPresupuestalEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AfectacionPresupuestalMapper {

    @Mappings({
        @Mapping(source = "rubro_presupuestal_id", target = "rubroPresupuestalId"),
        @Mapping(source = "tipo_operacion", target = "tipoOperacion"),
        @Mapping(source = "tercero_id", target = "terceroId"),
        @Mapping(source = "centro_costo_id", target = "centroCostoId"),
        @Mapping(source = "proyecto_id", target = "proyectoId"),
        @Mapping(source = "fuente_financiamiento_id", target = "fuenteFinanciamientoId"),
        @Mapping(source = "cpc_id", target = "cpcId"),
        @Mapping(source = "origen_modulo", target = "origenModulo"),
        @Mapping(source = "origen_id", target = "origenId"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    AfectacionPresupuestalResponseDto toResponseDto(AfectacionPresupuestalEntity entity);
}
