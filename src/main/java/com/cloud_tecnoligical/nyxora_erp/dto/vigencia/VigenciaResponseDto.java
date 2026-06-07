package com.cloud_tecnoligical.nyxora_erp.dto.vigencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VigenciaResponseDto {
    private Long id;
    private Integer year;
    private String status;       // planeada | abierta | en_cierre | cerrada
    private LocalDate openDate;
    private LocalDate closeDate;
    private Boolean active;
    private LocalDateTime createdAt;
}
