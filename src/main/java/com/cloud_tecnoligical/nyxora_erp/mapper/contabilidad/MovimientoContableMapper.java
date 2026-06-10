package com.cloud_tecnoligical.nyxora_erp.mapper.contabilidad;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateMovimientoContableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.MovimientoContableEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MovimientoContableMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "comprobante_id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(source = "cuentaId", target = "cuenta_id"),
        @Mapping(source = "terceroId", target = "tercero_id"),
        @Mapping(source = "centroCostoId", target = "centro_costo_id"),
        @Mapping(source = "proyectoId", target = "proyecto_id"),
        @Mapping(source = "recursoId", target = "recurso_id"),
        @Mapping(source = "valorBase", target = "valor_base"),
        @Mapping(source = "impuestoId", target = "impuesto_id"),
        @Mapping(source = "porcentajeImpuesto", target = "porcentaje_impuesto"),
        @Mapping(source = "valorTrm", target = "valor_trm"),
        @Mapping(source = "valorDolar", target = "valor_dolar")
    })
    MovimientoContableEntity toEntity(CreateMovimientoContableDto dto);
}
