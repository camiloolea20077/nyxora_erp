package com.cloud_tecnoligical.nyxora_erp.mapper.caja;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateReciboCajaPagoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ReciboCajaPagoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReciboCajaPagoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "recibo_caja_id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "formaPagoId", target = "forma_pago_id"),
        @Mapping(source = "bancoId", target = "banco_id"),
        @Mapping(source = "numeroCheque", target = "numero_cheque"),
        @Mapping(source = "numeroTarjeta", target = "numero_tarjeta"),
        @Mapping(source = "cuentaBancaria", target = "cuenta_bancaria")
    })
    ReciboCajaPagoEntity toEntity(CreateReciboCajaPagoDto dto);

    @Mappings({
        @Mapping(source = "recibo_caja_id", target = "reciboCajaId"),
        @Mapping(source = "forma_pago_id", target = "formaPagoId"),
        @Mapping(source = "banco_id", target = "bancoId"),
        @Mapping(source = "numero_cheque", target = "numeroCheque"),
        @Mapping(source = "numero_tarjeta", target = "numeroTarjeta"),
        @Mapping(source = "cuenta_bancaria", target = "cuentaBancaria")
    })
    ReciboCajaPagoResponseDto toResponseDto(ReciboCajaPagoEntity entity);
}
