package com.cloud_tecnoligical.nyxora_erp.mapper.adjunto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.adjunto.AdjuntoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.adjunto.CreateAdjuntoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.AdjuntoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AdjuntoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(source = "entidadId", target = "entidad_id"),
        @Mapping(source = "tipoMime", target = "tipo_mime"),
        @Mapping(source = "tamanoBytes", target = "tamano_bytes")
    })
    AdjuntoEntity toEntity(CreateAdjuntoRequestDto dto);

    @Mappings({
        @Mapping(source = "entidad_id", target = "entidadId"),
        @Mapping(source = "tipo_mime", target = "tipoMime"),
        @Mapping(source = "tamano_bytes", target = "tamanoBytes"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    AdjuntoResponseDto toResponseDto(AdjuntoEntity entity);
}
