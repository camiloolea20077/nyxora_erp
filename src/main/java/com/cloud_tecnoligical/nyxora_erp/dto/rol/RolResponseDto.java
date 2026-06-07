package com.cloud_tecnoligical.nyxora_erp.dto.rol;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolResponseDto {
    private Long id;
    private String name;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<Long> permisoIds;
}
