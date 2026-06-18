package com.cloud_tecnoligical.nyxora_erp.mapper.nomina;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ConceptoNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateConceptoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ConceptoNominaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConceptoNominaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "cuentaCreditoId", target = "cuenta_credito_id"),
        @Mapping(source = "cuentaPatronoId", target = "cuenta_patrono_id"),
        @Mapping(source = "rubroPresupuestalId", target = "rubro_presupuestal_id"),
        @Mapping(source = "fuenteFinanciamientoId", target = "fuente_financiamiento_id"),
        @Mapping(source = "terceroId", target = "tercero_id"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    ConceptoNominaEntity toEntity(CreateConceptoNominaRequestDto dto);

    @Mappings({
        @Mapping(source = "cuenta_credito_id", target = "cuentaCreditoId"),
        @Mapping(source = "cuenta_patrono_id", target = "cuentaPatronoId"),
        @Mapping(source = "rubro_presupuestal_id", target = "rubroPresupuestalId"),
        @Mapping(source = "fuente_financiamiento_id", target = "fuenteFinanciamientoId"),
        @Mapping(source = "tercero_id", target = "terceroId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ConceptoNominaResponseDto toResponseDto(ConceptoNominaEntity entity);
}
