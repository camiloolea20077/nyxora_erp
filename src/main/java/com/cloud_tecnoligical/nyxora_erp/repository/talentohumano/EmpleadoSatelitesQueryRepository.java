package com.cloud_tecnoligical.nyxora_erp.repository.talentohumano;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoEstudioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoFamiliarResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoHistoriaLaboralResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

/** Listados de los satélites del empleado (estudios, familiares, historia laboral). */
@Repository
public class EmpleadoSatelitesQueryRepository {

    private final DatabaseClient db;

    public EmpleadoSatelitesQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    // ---------- Estudios ----------
    public Mono<List<EmpleadoEstudioResponseDto>> listEstudios(Long empleadoId, Long empresaId) {
        return db.sql("""
                SELECT id AS "id", empleado_id AS "empleadoId", nivel_estudio_id AS "nivelEstudioId",
                       institucion AS "institucion", titulo AS "titulo", fecha_inicial AS "fechaInicial",
                       fecha_final AS "fechaFinal", fecha_grado AS "fechaGrado",
                       numero_tarjeta_profesional AS "numeroTarjetaProfesional",
                       municipio_estudio_id AS "municipioEstudioId", semestres_aprobados AS "semestresAprobados",
                       convalidado AS "convalidado", activo AS "active"
                FROM empleado_estudio WHERE empleado_id=:emp AND empresa_id=:e AND deleted_at IS NULL
                ORDER BY fecha_inicial DESC NULLS LAST, id
                """)
            .bind("emp", empleadoId).bind("e", empresaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, EmpleadoEstudioResponseDto.class))
            .collectList();
    }

    // ---------- Familiares ----------
    public Mono<List<EmpleadoFamiliarResponseDto>> listFamiliares(Long empleadoId, Long empresaId) {
        return db.sql("""
                SELECT id AS "id", empleado_id AS "empleadoId", nombre_apellido AS "nombreApellido",
                       fecha_nacimiento AS "fechaNacimiento", parentesco AS "parentesco", a_cargo AS "aCargo",
                       vivo AS "vivo", convive AS "convive", dependiente_retencion AS "dependienteRetencion"
                FROM empleado_familiar WHERE empleado_id=:emp AND empresa_id=:e AND deleted_at IS NULL
                ORDER BY id
                """)
            .bind("emp", empleadoId).bind("e", empresaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, EmpleadoFamiliarResponseDto.class))
            .collectList();
    }

    // ---------- Historia laboral ----------
    public Mono<List<EmpleadoHistoriaLaboralResponseDto>> listHistorias(Long empleadoId, Long empresaId) {
        return db.sql("""
                SELECT id AS "id", empleado_id AS "empleadoId", nombre_empresa AS "nombreEmpresa",
                       cargo AS "cargo", tipo_contrato AS "tipoContrato", fecha_inicio AS "fechaInicio",
                       fecha_final AS "fechaFinal", jefe_inmediato AS "jefeInmediato",
                       municipio_id AS "municipioId", es_publico AS "esPublico"
                FROM empleado_historia_laboral WHERE empleado_id=:emp AND empresa_id=:e AND deleted_at IS NULL
                ORDER BY fecha_inicio DESC NULLS LAST, id
                """)
            .bind("emp", empleadoId).bind("e", empresaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, EmpleadoHistoriaLaboralResponseDto.class))
            .collectList();
    }
}
