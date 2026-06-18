package com.cloud_tecnoligical.nyxora_erp.mapper.academico;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateProgramaAcademicoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.ProgramaAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProgramaAcademicoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProgramaAcademicoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "tipoPrograma", target = "tipo_programa"),
        @Mapping(source = "centroCostoProgramaId", target = "centro_costo_programa_id"),
        @Mapping(source = "centroCostoFacultadId", target = "centro_costo_facultad_id"),
        @Mapping(source = "registroAcademico", target = "registro_academico"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    ProgramaAcademicoEntity toEntity(CreateProgramaAcademicoRequestDto dto);

    @Mappings({
        @Mapping(source = "tipo_programa", target = "tipoPrograma"),
        @Mapping(source = "centro_costo_programa_id", target = "centroCostoProgramaId"),
        @Mapping(source = "centro_costo_facultad_id", target = "centroCostoFacultadId"),
        @Mapping(source = "registro_academico", target = "registroAcademico"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ProgramaAcademicoResponseDto toResponseDto(ProgramaAcademicoEntity entity);
}
