package com.cloud_tecnoligical.nyxora_erp.mapper.tercero;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroCuentaBancariaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroCuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroCuentaBancariaDto;
import com.cloud_tecnoligical.nyxora_erp.entity.TerceroCuentaBancariaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TerceroCuentaBancariaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "tercero_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "bancoId", target = "banco_id"),
        @Mapping(source = "tipoCuentaBancariaId", target = "tipo_cuenta_bancaria_id"),
        @Mapping(source = "numeroCuenta", target = "numero_cuenta")
    })
    TerceroCuentaBancariaEntity toEntity(CreateTerceroCuentaBancariaDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "tercero_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "bancoId", target = "banco_id"),
        @Mapping(source = "tipoCuentaBancariaId", target = "tipo_cuenta_bancaria_id"),
        @Mapping(source = "numeroCuenta", target = "numero_cuenta")
    })
    void updateEntityFromDto(UpdateTerceroCuentaBancariaDto dto, @MappingTarget TerceroCuentaBancariaEntity entity);

    @Mappings({
        @Mapping(source = "tercero_id", target = "terceroId"),
        @Mapping(source = "banco_id", target = "bancoId"),
        @Mapping(source = "tipo_cuenta_bancaria_id", target = "tipoCuentaBancariaId"),
        @Mapping(source = "numero_cuenta", target = "numeroCuenta"),
        @Mapping(source = "activo", target = "active")
    })
    TerceroCuentaBancariaResponseDto toResponseDto(TerceroCuentaBancariaEntity entity);
}
