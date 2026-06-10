package com.cloud_tecnoligical.nyxora_erp.mapper.impuesto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.CreateImpuestoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.ImpuestoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.impuesto.UpdateImpuestoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ImpuestoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ImpuestoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "baseGravable", target = "base_gravable"),
        @Mapping(source = "aplicaAiu", target = "aplica_aiu"),
        @Mapping(source = "retencionNomina", target = "retencion_nomina"),
        @Mapping(source = "vigenciaId", target = "vigencia_id"),
        @Mapping(source = "cuentaCompraId", target = "cuenta_compra_id"),
        @Mapping(source = "cuentaVentaId", target = "cuenta_venta_id")
        // codigo, nombre, tipo, causacion, periodicidad, tarifa por nombre igual
    })
    ImpuestoEntity toEntity(CreateImpuestoRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "baseGravable", target = "base_gravable"),
        @Mapping(source = "aplicaAiu", target = "aplica_aiu"),
        @Mapping(source = "retencionNomina", target = "retencion_nomina"),
        @Mapping(source = "vigenciaId", target = "vigencia_id"),
        @Mapping(source = "cuentaCompraId", target = "cuenta_compra_id"),
        @Mapping(source = "cuentaVentaId", target = "cuenta_venta_id")
    })
    void updateEntityFromDto(UpdateImpuestoRequestDto dto, @MappingTarget ImpuestoEntity entity);

    @Mappings({
        @Mapping(source = "base_gravable", target = "baseGravable"),
        @Mapping(source = "aplica_aiu", target = "aplicaAiu"),
        @Mapping(source = "retencion_nomina", target = "retencionNomina"),
        @Mapping(source = "vigencia_id", target = "vigenciaId"),
        @Mapping(source = "cuenta_compra_id", target = "cuentaCompraId"),
        @Mapping(source = "cuenta_venta_id", target = "cuentaVentaId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ImpuestoResponseDto toResponseDto(ImpuestoEntity entity);
}
