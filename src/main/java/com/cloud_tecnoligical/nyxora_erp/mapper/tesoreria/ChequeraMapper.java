package com.cloud_tecnoligical.nyxora_erp.mapper.tesoreria;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ChequeraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateChequeraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ChequeraEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChequeraMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "consecutivo_actual", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "cuentaBancariaId", target = "cuenta_bancaria_id"),
        @Mapping(source = "fechaExpedicion", target = "fecha_expedicion"),
        @Mapping(source = "numeroInicial", target = "numero_inicial"),
        @Mapping(source = "numeroFinal", target = "numero_final")
    })
    ChequeraEntity toEntity(CreateChequeraRequestDto dto);

    @Mappings({
        @Mapping(source = "cuenta_bancaria_id", target = "cuentaBancariaId"),
        @Mapping(source = "fecha_expedicion", target = "fechaExpedicion"),
        @Mapping(source = "numero_inicial", target = "numeroInicial"),
        @Mapping(source = "numero_final", target = "numeroFinal"),
        @Mapping(source = "consecutivo_actual", target = "consecutivoActual"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ChequeraResponseDto toResponseDto(ChequeraEntity entity);
}
