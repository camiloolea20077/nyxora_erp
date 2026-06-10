package com.cloud_tecnoligical.nyxora_erp.mapper.documento;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.documento.CreateTipoDocumentoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.TipoDocumentoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.UpdateTipoDocumentoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.TipoDocumentoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TipoDocumentoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "reiniciaPorVigencia", target = "reinicia_por_vigencia")
    })
    TipoDocumentoEntity toEntity(CreateTipoDocumentoRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "reiniciaPorVigencia", target = "reinicia_por_vigencia")
    })
    void updateEntityFromDto(UpdateTipoDocumentoRequestDto dto, @MappingTarget TipoDocumentoEntity entity);

    @Mappings({
        @Mapping(source = "reinicia_por_vigencia", target = "reiniciaPorVigencia"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    TipoDocumentoResponseDto toResponseDto(TipoDocumentoEntity entity);
}
