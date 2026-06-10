package com.cloud_tecnoligical.nyxora_erp.repository.tercero;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroContactoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroCuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroDireccionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

/** Listados y utilidades (desmarcar principal) de los satélites del tercero. */
@Repository
public class TerceroSatelitesQueryRepository {

    private final DatabaseClient db;

    public TerceroSatelitesQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    // ---------- Contactos ----------
    public Mono<List<TerceroContactoResponseDto>> listContactos(Long terceroId) {
        return db.sql("""
                SELECT id AS "id", tercero_id AS "terceroId", nombre AS "nombre", cargo AS "cargo",
                       telefono AS "telefono", celular AS "celular", email AS "email", notas AS "notas",
                       principal AS "principal", activo AS "active"
                FROM tercero_contacto WHERE tercero_id=:t AND deleted_at IS NULL
                ORDER BY principal DESC, id
                """)
            .bind("t", terceroId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, TerceroContactoResponseDto.class))
            .collectList();
    }

    public Mono<Long> unsetPrincipalContacto(Long terceroId) {
        return db.sql("UPDATE tercero_contacto SET principal=false WHERE tercero_id=:t AND deleted_at IS NULL")
            .bind("t", terceroId).fetch().rowsUpdated();
    }

    // ---------- Direcciones ----------
    public Mono<List<TerceroDireccionResponseDto>> listDirecciones(Long terceroId) {
        return db.sql("""
                SELECT id AS "id", tercero_id AS "terceroId", tipo AS "tipo", direccion AS "direccion",
                       municipio_id AS "municipioId", barrio_id AS "barrioId", codigo_postal AS "codigoPostal",
                       telefono AS "telefono", principal AS "principal", activo AS "active"
                FROM tercero_direccion WHERE tercero_id=:t AND deleted_at IS NULL
                ORDER BY principal DESC, id
                """)
            .bind("t", terceroId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, TerceroDireccionResponseDto.class))
            .collectList();
    }

    public Mono<Long> unsetPrincipalDireccion(Long terceroId) {
        return db.sql("UPDATE tercero_direccion SET principal=false WHERE tercero_id=:t AND deleted_at IS NULL")
            .bind("t", terceroId).fetch().rowsUpdated();
    }

    // ---------- Cuentas bancarias ----------
    public Mono<List<TerceroCuentaBancariaResponseDto>> listCuentas(Long terceroId) {
        return db.sql("""
                SELECT id AS "id", tercero_id AS "terceroId", banco_id AS "bancoId",
                       tipo_cuenta_bancaria_id AS "tipoCuentaBancariaId", numero_cuenta AS "numeroCuenta",
                       principal AS "principal", activo AS "active"
                FROM tercero_cuenta_bancaria WHERE tercero_id=:t AND deleted_at IS NULL
                ORDER BY principal DESC, id
                """)
            .bind("t", terceroId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, TerceroCuentaBancariaResponseDto.class))
            .collectList();
    }

    public Mono<Long> unsetPrincipalCuenta(Long terceroId) {
        return db.sql("UPDATE tercero_cuenta_bancaria SET principal=false WHERE tercero_id=:t AND deleted_at IS NULL")
            .bind("t", terceroId).fetch().rowsUpdated();
    }
}
