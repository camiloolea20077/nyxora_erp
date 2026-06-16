package com.cloud_tecnoligical.nyxora_erp.mapper.caja;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.CajaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CajaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CajaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "estado", ignore = true),
        @Mapping(target = "saldo_inicial", ignore = true),
        @Mapping(target = "fecha_apertura", ignore = true),
        @Mapping(target = "fecha_cierre", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "sedeId", target = "sede_id"),
        @Mapping(source = "usuarioId", target = "usuario_id")
    })
    CajaEntity toEntity(CreateCajaRequestDto dto);

    @Mappings({
        @Mapping(source = "sede_id", target = "sedeId"),
        @Mapping(source = "usuario_id", target = "usuarioId"),
        @Mapping(source = "saldo_inicial", target = "saldoInicial"),
        @Mapping(source = "fecha_apertura", target = "fechaApertura"),
        @Mapping(source = "fecha_cierre", target = "fechaCierre"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    CajaResponseDto toResponseDto(CajaEntity entity);
}
