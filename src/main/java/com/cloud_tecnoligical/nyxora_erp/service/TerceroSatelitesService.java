package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroContactoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroCuentaBancariaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroDireccionDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroContactoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroCuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroDireccionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroContactoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroCuentaBancariaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroDireccionDto;

import reactor.core.publisher.Mono;

public interface TerceroSatelitesService {
    // Contactos
    Mono<List<TerceroContactoResponseDto>> listContactos(Long terceroId);
    Mono<TerceroContactoResponseDto> createContacto(Long terceroId, CreateTerceroContactoDto dto);
    Mono<Boolean> updateContacto(Long terceroId, UpdateTerceroContactoDto dto);
    Mono<Boolean> deleteContacto(Long terceroId, Long id);
    // Direcciones
    Mono<List<TerceroDireccionResponseDto>> listDirecciones(Long terceroId);
    Mono<TerceroDireccionResponseDto> createDireccion(Long terceroId, CreateTerceroDireccionDto dto);
    Mono<Boolean> updateDireccion(Long terceroId, UpdateTerceroDireccionDto dto);
    Mono<Boolean> deleteDireccion(Long terceroId, Long id);
    // Cuentas bancarias
    Mono<List<TerceroCuentaBancariaResponseDto>> listCuentas(Long terceroId);
    Mono<TerceroCuentaBancariaResponseDto> createCuenta(Long terceroId, CreateTerceroCuentaBancariaDto dto);
    Mono<Boolean> updateCuenta(Long terceroId, UpdateTerceroCuentaBancariaDto dto);
    Mono<Boolean> deleteCuenta(Long terceroId, Long id);
}
