package com.cloud_tecnoligical.nyxora_erp.dto.parametro;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParametroResponseDto {
    private Long id;
    private String key;
    private String value;
    private String dataType;
    private Boolean active;
    private LocalDateTime createdAt;
}
