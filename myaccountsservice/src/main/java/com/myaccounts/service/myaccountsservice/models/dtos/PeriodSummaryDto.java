package com.myaccounts.service.myaccountsservice.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodSummaryDto {
    private Long id;
    private String created;
    private BigDecimal totalIncomes;
    private BigDecimal totalCost;
    private BigDecimal total;
}