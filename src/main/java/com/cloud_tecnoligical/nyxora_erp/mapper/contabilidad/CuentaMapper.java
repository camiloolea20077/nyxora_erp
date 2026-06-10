package com.cloud_tecnoligical.nyxora_erp.mapper.contabilidad;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateCuentaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CuentaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.UpdateCuentaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CuentaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CuentaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "cuentaPadreId", target = "cuenta_padre_id"),
        @Mapping(source = "codigoCuenta", target = "codigo_cuenta"),
        @Mapping(source = "nombreCuenta", target = "nombre_cuenta"),
        @Mapping(source = "tipoCuenta", target = "tipo_cuenta"),
        @Mapping(source = "manejaMovimiento", target = "maneja_movimiento"),
        @Mapping(source = "manejaMovimientoManual", target = "maneja_movimiento_manual"),
        @Mapping(source = "manejaTercero", target = "maneja_tercero"),
        @Mapping(source = "manejaCentroCosto", target = "maneja_centro_costo"),
        @Mapping(source = "manejaImpuesto", target = "maneja_impuesto"),
        @Mapping(source = "manejaProyecto", target = "maneja_proyecto"),
        @Mapping(source = "manejaRecurso", target = "maneja_recurso"),
        @Mapping(source = "manejaSaldoContrario", target = "maneja_saldo_contrario"),
        @Mapping(source = "esCorriente", target = "es_corriente")
    })
    CuentaEntity toEntity(CreateCuentaRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "cuentaPadreId", target = "cuenta_padre_id"),
        @Mapping(source = "codigoCuenta", target = "codigo_cuenta"),
        @Mapping(source = "nombreCuenta", target = "nombre_cuenta"),
        @Mapping(source = "tipoCuenta", target = "tipo_cuenta"),
        @Mapping(source = "manejaMovimiento", target = "maneja_movimiento"),
        @Mapping(source = "manejaMovimientoManual", target = "maneja_movimiento_manual"),
        @Mapping(source = "manejaTercero", target = "maneja_tercero"),
        @Mapping(source = "manejaCentroCosto", target = "maneja_centro_costo"),
        @Mapping(source = "manejaImpuesto", target = "maneja_impuesto"),
        @Mapping(source = "manejaProyecto", target = "maneja_proyecto"),
        @Mapping(source = "manejaRecurso", target = "maneja_recurso"),
        @Mapping(source = "manejaSaldoContrario", target = "maneja_saldo_contrario"),
        @Mapping(source = "esCorriente", target = "es_corriente")
    })
    void updateEntityFromDto(UpdateCuentaRequestDto dto, @MappingTarget CuentaEntity entity);

    @Mappings({
        @Mapping(source = "cuenta_padre_id", target = "cuentaPadreId"),
        @Mapping(source = "codigo_cuenta", target = "codigoCuenta"),
        @Mapping(source = "nombre_cuenta", target = "nombreCuenta"),
        @Mapping(source = "tipo_cuenta", target = "tipoCuenta"),
        @Mapping(source = "maneja_movimiento", target = "manejaMovimiento"),
        @Mapping(source = "maneja_movimiento_manual", target = "manejaMovimientoManual"),
        @Mapping(source = "maneja_tercero", target = "manejaTercero"),
        @Mapping(source = "maneja_centro_costo", target = "manejaCentroCosto"),
        @Mapping(source = "maneja_impuesto", target = "manejaImpuesto"),
        @Mapping(source = "maneja_proyecto", target = "manejaProyecto"),
        @Mapping(source = "maneja_recurso", target = "manejaRecurso"),
        @Mapping(source = "maneja_saldo_contrario", target = "manejaSaldoContrario"),
        @Mapping(source = "es_corriente", target = "esCorriente"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    CuentaResponseDto toResponseDto(CuentaEntity entity);
}
