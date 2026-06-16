package com.cloud_tecnoligical.nyxora_erp.mapper.caja;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ReciboCajaLineaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReciboCajaLineaMapper {

    @Mappings({
        @Mapping(source = "recibo_caja_id", target = "reciboCajaId"),
        @Mapping(source = "cuenta_por_cobrar_id", target = "cuentaPorCobrarId"),
        @Mapping(source = "valor_aplicado", target = "valorAplicado")
    })
    ReciboCajaLineaResponseDto toResponseDto(ReciboCajaLineaEntity entity);
}
