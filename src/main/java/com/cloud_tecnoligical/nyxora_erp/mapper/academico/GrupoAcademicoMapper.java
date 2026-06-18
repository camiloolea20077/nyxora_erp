package com.cloud_tecnoligical.nyxora_erp.mapper.academico;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateGrupoAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GrupoAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.GrupoAcademicoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GrupoAcademicoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "programaAcademicoId", target = "programa_academico_id"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    GrupoAcademicoEntity toEntity(CreateGrupoAcademicoRequestDto dto);

    @Mappings({
        @Mapping(source = "programa_academico_id", target = "programaAcademicoId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    GrupoAcademicoResponseDto toResponseDto(GrupoAcademicoEntity entity);
}
