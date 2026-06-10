package com.cloud_tecnoligical.nyxora_erp.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.documento.ConsecutivoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.ConsecutivoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.TipoDocumentoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.repository.documento.ConsecutivoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.documento.TipoDocumentoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ConsecutivoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class ConsecutivoServiceImpl implements ConsecutivoService {

    private final TipoDocumentoQueryRepository tipoDocumentoQueryRepository;
    private final ConsecutivoQueryRepository consecutivoQueryRepository;
    private final TransactionalOperator txOperator;

    public ConsecutivoServiceImpl(TipoDocumentoQueryRepository tipoDocumentoQueryRepository,
                                  ConsecutivoQueryRepository consecutivoQueryRepository,
                                  ReactiveTransactionManager transactionManager) {
        this.tipoDocumentoQueryRepository = tipoDocumentoQueryRepository;
        this.consecutivoQueryRepository = consecutivoQueryRepository;
        this.txOperator = TransactionalOperator.create(transactionManager);
    }

    @Override
    public Mono<ConsecutivoResponseDto> siguiente(Long tipoDocumentoId, ConsecutivoRequestDto request) {
        return TenantContext.get().flatMap(t -> {
            Long sedeId = request.getSedeId() != null ? request.getSedeId() : t.getSedeId();
            if (sedeId == null) {
                return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "No hay sede en el contexto para el consecutivo"));
            }
            Long vigenciaId = request.getVigenciaId();
            // 1) validar que el tipo de documento es de la empresa
            return tipoDocumentoQueryRepository.findActiveById(tipoDocumentoId, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Tipo de documento no encontrado")))
                .flatMap(tipo ->
                    // 2) incremento atómico bajo transacción (FOR UPDATE)
                    consecutivoQueryRepository.incrementarYObtener(tipoDocumentoId, sedeId, vigenciaId)
                        .as(txOperator::transactional)
                        .map(numero -> new ConsecutivoResponseDto(
                            tipoDocumentoId, sedeId, vigenciaId, numero, formatear(tipo, numero))));
        });
    }

    private String formatear(TipoDocumentoResponseDto tipo, Long numero) {
        String prefijo = tipo.getPrefijo() != null ? tipo.getPrefijo() : "";
        return prefijo + numero;
    }
}
