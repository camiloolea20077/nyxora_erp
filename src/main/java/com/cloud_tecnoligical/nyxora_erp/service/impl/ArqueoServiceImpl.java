package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.ArqueoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ArqueoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateArqueoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ArqueoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.caja.ArqueoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.ArqueoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.ArqueoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.CajaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.ReciboCajaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ArqueoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ArqueoServiceImpl implements ArqueoService {

    private final ArqueoR2dbcRepository repo;
    private final ArqueoQueryRepository queryRepo;
    private final CajaQueryRepository cajaQuery;
    private final ReciboCajaQueryRepository reciboQuery;
    private final ArqueoMapper mapper;

    public ArqueoServiceImpl(ArqueoR2dbcRepository repo, ArqueoQueryRepository queryRepo,
                             CajaQueryRepository cajaQuery, ReciboCajaQueryRepository reciboQuery,
                             ArqueoMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.cajaQuery = cajaQuery;
        this.reciboQuery = reciboQuery;
        this.mapper = mapper;
    }

    @Override
    public Mono<ArqueoResponseDto> create(CreateArqueoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cajaQuery.findActiveById(dto.getCajaId(), t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La caja no existe")))
                .flatMap(caja -> reciboQuery.totalRecaudadoCaja(dto.getCajaId(), t.getEmpresaId()).flatMap(recaudado -> {
                    BigDecimal valorSistema = nz(caja.getSaldoInicial()).add(nz(recaudado));
                    BigDecimal diferencia = nz(dto.getValorDeclarado()).subtract(valorSistema);
                    ArqueoEntity e = new ArqueoEntity();
                    e.setEmpresa_id(t.getEmpresaId());
                    e.setCaja_id(dto.getCajaId());
                    e.setFecha(LocalDateTime.now());
                    e.setValor_declarado(dto.getValorDeclarado());
                    e.setValor_sistema(valorSistema);
                    e.setDiferencia(diferencia);
                    e.setObservaciones(dto.getObservaciones());
                    e.setUsuario_creacion(t.getUsuarioId());
                    e.setCreated_at(LocalDateTime.now());
                    return repo.save(e).flatMap(saved -> findById(saved.getId()));
                })));
    }

    @Override
    public Mono<ArqueoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Arqueo no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<ArqueoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
