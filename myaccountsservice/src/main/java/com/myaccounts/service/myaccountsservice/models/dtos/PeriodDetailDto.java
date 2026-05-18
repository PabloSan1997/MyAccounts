package com.myaccounts.service.myaccountsservice.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodDetailDto {
    private Long id;
    private String created;
    private List<ItemDto> variableCosts;
    private List<ItemDto> variableIncomes;
    private List<ItemDto> fixedCosts;
    private List<ItemDto> fixedIncomes;
}