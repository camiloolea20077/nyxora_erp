package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ConceptoNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.ConceptoNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateConceptoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateConceptoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ConceptoNominaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.nomina.ConceptoNominaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.ConceptoNominaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.ConceptoNominaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ConceptoNominaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ConceptoNominaServiceImpl implements ConceptoNominaService {

    private static final Set<String> CLASES = Set.of("devengado", "deduccion", "provision", "aporte");

    private final ConceptoNominaR2dbcRepository repo;
    private final ConceptoNominaQueryRepository queryRepo;
    private final ConceptoNominaMapper mapper;

    public ConceptoNominaServiceImpl(ConceptoNominaR2dbcRepository repo, ConceptoNominaQueryRepository queryRepo,
                                     ConceptoNominaMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<ConceptoNominaResponseDto> create(CreateConceptoNominaRequestDto dto) {
        return validarClase(dto.getClase()).then(TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un concepto con ese código"));
                }
                ConceptoNominaEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateConceptoNominaRequestDto dto) {
        return validarClase(dto.getClase()).then(TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un concepto con ese código"));
                    }
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    e.setFrecuencia(dto.getFrecuencia());
                    e.setClase(dto.getClase());
                    e.setFormula(dto.getFormula());
                    e.setCuenta_credito_id(dto.getCuentaCreditoId());
                    e.setCuenta_patrono_id(dto.getCuentaPatronoId());
                    e.setRubro_presupuestal_id(dto.getRubroPresupuestalId());
                    e.setFuente_financiamiento_id(dto.getFuenteFinanciamientoId());
                    e.setTercero_id(dto.getTerceroId());
                    if (dto.getActive() != null) e.setActivo(dto.getActive());
                    e.setUpdated_at(LocalDateTime.now());
                    return repo.save(e).thenReturn(true);
                }))));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setDeleted_at(LocalDateTime.now());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<ConceptoNominaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Concepto no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<ConceptoNominaTableDto>> list(PageableDto<?> request, String clase) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId(), clase));
    }

    private Mono<Void> validarClase(String clase) {
        if (clase == null || !CLASES.contains(clase.trim())) {
            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                "Clase inválida (devengado, deduccion, provision o aporte)"));
        }
        return Mono.empty();
    }

    private Mono<ConceptoNominaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Concepto no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Concepto no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
