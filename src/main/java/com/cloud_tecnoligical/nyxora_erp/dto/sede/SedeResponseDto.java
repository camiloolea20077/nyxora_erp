package com.cloud_tecnoligical.nyxora_erp.dto.sede;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedeResponseDto {
    private Long id;
    private String code;
    private String name;
    private Boolean active;
    private LocalDateTime createdAt;
}
