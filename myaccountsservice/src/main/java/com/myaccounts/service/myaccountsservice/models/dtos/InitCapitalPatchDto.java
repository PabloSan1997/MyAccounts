package com.myaccounts.service.myaccountsservice.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitCapitalPatchDto {
    @NotNull(message = "initValue is required")
    private BigDecimal initValue;
}