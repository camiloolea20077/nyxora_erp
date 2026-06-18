package com.cloud_tecnoligical.nyxora_erp.mapper.nomina;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateNovedadNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.NovedadNominaEntity;

/** Aplica los campos editables de la novedad sobre la entidad. */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NovedadNominaMapper {

    @Mappings({
        @Mapping(source = "vinculacionId", target = "vinculacion_id"),
        @Mapping(source = "conceptoNominaId", target = "concepto_nomina_id"),
        @Mapping(source = "terceroId", target = "tercero_id"),
        @Mapping(source = "cantidadValor", target = "cantidad_valor"),
        @Mapping(source = "fechaInicial", target = "fecha_inicial"),
        @Mapping(source = "fechaFinal", target = "fecha_final"),
        @Mapping(source = "fechaAplicada", target = "fecha_aplicada"),
        @Mapping(source = "numeroCuota", target = "numero_cuota"),
        @Mapping(source = "tipoAusentismo", target = "tipo_ausentismo"),
        @Mapping(source = "tipoEmbargo", target = "tipo_embargo"),
        @Mapping(source = "bancoId", target = "banco_id"),
        @Mapping(source = "numeroCuentaBancaria", target = "numero_cuenta_bancaria"),
        @Mapping(source = "estadoNovedad", target = "estado_novedad"),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "anulado", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true)
    })
    void apply(CreateNovedadNominaRequestDto dto, @MappingTarget NovedadNominaEntity entity);
}
