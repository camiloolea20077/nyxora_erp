package com.cloud_tecnoligical.nyxora_erp.mapper.recurso;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.recurso.CreateRecursoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.RecursoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.recurso.UpdateRecursoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.RecursoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecursoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "tipoRecurso", target = "tipo_recurso"),
        @Mapping(source = "costoAdicional", target = "costo_adicional")
    })
    RecursoEntity toEntity(CreateRecursoRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "tipoRecurso", target = "tipo_recurso"),
        @Mapping(source = "costoAdicional", target = "costo_adicional")
    })
    void updateEntityFromDto(UpdateRecursoRequestDto dto, @MappingTarget RecursoEntity entity);

    @Mappings({
        @Mapping(source = "tipo_recurso", target = "tipoRecurso"),
        @Mapping(source = "costo_adicional", target = "costoAdicional"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    RecursoResponseDto toResponseDto(RecursoEntity entity);
}
