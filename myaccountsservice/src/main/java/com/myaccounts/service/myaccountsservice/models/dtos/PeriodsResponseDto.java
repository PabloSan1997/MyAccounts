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
public class PeriodsResponseDto {
    private List<PeriodSummaryDto> periods;
}