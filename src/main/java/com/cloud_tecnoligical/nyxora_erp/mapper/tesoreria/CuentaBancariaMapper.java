package com.cloud_tecnoligical.nyxora_erp.mapper.tesoreria;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateCuentaBancariaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CuentaBancariaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CuentaBancariaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "bancoId", target = "banco_id"),
        @Mapping(source = "tipoCuentaBancariaId", target = "tipo_cuenta_bancaria_id"),
        @Mapping(source = "numeroCuenta", target = "numero_cuenta"),
        @Mapping(source = "cuentaContableId", target = "cuenta_contable_id"),
        @Mapping(source = "manejaSobregiro", target = "maneja_sobregiro"),
        @Mapping(source = "aceptaTransferencias", target = "acepta_transferencias"),
        @Mapping(source = "fechaExpiracion", target = "fecha_expiracion")
    })
    CuentaBancariaEntity toEntity(CreateCuentaBancariaRequestDto dto);

    @Mappings({
        @Mapping(source = "banco_id", target = "bancoId"),
        @Mapping(source = "tipo_cuenta_bancaria_id", target = "tipoCuentaBancariaId"),
        @Mapping(source = "numero_cuenta", target = "numeroCuenta"),
        @Mapping(source = "cuenta_contable_id", target = "cuentaContableId"),
        @Mapping(source = "maneja_sobregiro", target = "manejaSobregiro"),
        @Mapping(source = "acepta_transferencias", target = "aceptaTransferencias"),
        @Mapping(source = "fecha_expiracion", target = "fechaExpiracion"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    CuentaBancariaResponseDto toResponseDto(CuentaBancariaEntity entity);
}
