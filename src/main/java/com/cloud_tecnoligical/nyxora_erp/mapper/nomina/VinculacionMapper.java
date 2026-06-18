package com.cloud_tecnoligical.nyxora_erp.mapper.nomina;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateVinculacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.VinculacionEntity;

/** Aplica los campos editables de la vinculación sobre la entidad. */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VinculacionMapper {

    @Mappings({
        @Mapping(source = "empleadoId", target = "empleado_id"),
        @Mapping(source = "cargoId", target = "cargo_id"),
        @Mapping(source = "grupoNominaId", target = "grupo_nomina_id"),
        @Mapping(source = "fechaFin", target = "fecha_fin"),
        @Mapping(source = "tipoVinculacion", target = "tipo_vinculacion"),
        @Mapping(source = "tipoContrato", target = "tipo_contrato"),
        @Mapping(source = "horaTrabajo", target = "hora_trabajo"),
        @Mapping(source = "periodoPrueba", target = "periodo_prueba"),
        @Mapping(source = "fechaFinPeriodoPrueba", target = "fecha_fin_periodo_prueba"),
        @Mapping(source = "frecuenciaPago", target = "frecuencia_pago"),
        @Mapping(source = "jefeId", target = "jefe_id"),
        @Mapping(source = "sedeId", target = "sede_id"),
        @Mapping(source = "dependenciaId", target = "dependencia_id"),
        @Mapping(source = "municipioVinculacionId", target = "municipio_vinculacion_id"),
        @Mapping(source = "tipoCotizante", target = "tipo_cotizante"),
        @Mapping(source = "estadoVinculacion", target = "estado_vinculacion"),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true)
    })
    void apply(CreateVinculacionRequestDto dto, @MappingTarget VinculacionEntity entity);
}
