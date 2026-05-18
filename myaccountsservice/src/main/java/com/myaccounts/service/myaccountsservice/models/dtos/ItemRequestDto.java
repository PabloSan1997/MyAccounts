package com.myaccounts.service.myaccountsservice.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private BigDecimal value;
    private String title;
}