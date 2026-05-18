package com.myaccounts.service.myaccountsservice.services;

import com.myaccounts.service.myaccountsservice.models.dtos.ItemDto;
import com.myaccounts.service.myaccountsservice.models.dtos.ItemRequestDto;
import com.myaccounts.service.myaccountsservice.models.dtos.PeriodDetailDto;
import com.myaccounts.service.myaccountsservice.models.dtos.PeriodSummaryDto;
import com.myaccounts.service.myaccountsservice.models.dtos.PeriodsResponseDto;

public interface PeriodService {
    PeriodsResponseDto getPeriods();
    PeriodSummaryDto createPeriod();
    void deletePeriod(Long id);
    PeriodDetailDto getPeriodById(Long id);

    // Fixed Costs
    ItemDto createFixedCost(Long periodId, ItemRequestDto dto);
    ItemDto updateFixedCost(Long periodId, Long fixedCostId, ItemRequestDto dto);
    void deleteFixedCost(Long periodId, Long fixedCostId);

    // Fixed Incomes
    ItemDto createFixedIncome(Long periodId, ItemRequestDto dto);
    ItemDto updateFixedIncome(Long periodId, Long fixedIncomeId, ItemRequestDto dto);
    void deleteFixedIncome(Long periodId, Long fixedIncomeId);

    // Variable Costs
    ItemDto createVariableCost(Long periodId, ItemRequestDto dto);
    ItemDto updateVariableCost(Long periodId, Long variableCostId, ItemRequestDto dto);
    void deleteVariableCost(Long periodId, Long variableCostId);

    // Variable Incomes
    ItemDto createVariableIncome(Long periodId, ItemRequestDto dto);
    ItemDto updateVariableIncome(Long periodId, Long variableIncomeId, ItemRequestDto dto);
    void deleteVariableIncome(Long periodId, Long variableIncomeId);
}