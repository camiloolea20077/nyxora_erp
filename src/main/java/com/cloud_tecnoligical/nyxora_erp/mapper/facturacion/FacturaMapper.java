package com.cloud_tecnoligical.nyxora_erp.mapper.facturacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FacturaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FacturaMapper {

    @Mappings({
        @Mapping(source = "sede_id", target = "sedeId"),
        @Mapping(source = "vigencia_id", target = "vigenciaId"),
        @Mapping(source = "tipo_documento_id", target = "tipoDocumentoId"),
        @Mapping(source = "resolucion_dian_id", target = "resolucionDianId"),
        @Mapping(source = "cliente_id", target = "clienteId"),
        @Mapping(source = "bodega_id", target = "bodegaId"),
        @Mapping(source = "centro_costo_id", target = "centroCostoId"),
        @Mapping(source = "condicion_pago_id", target = "condicionPagoId"),
        @Mapping(source = "fecha_vencimiento", target = "fechaVencimiento"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "lineas", ignore = true)
    })
    FacturaResponseDto toResponseDto(FacturaEntity entity);
}
