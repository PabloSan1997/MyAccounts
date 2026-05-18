package com.myaccounts.service.myaccountsservice.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotNull(message = "Value is required")
    private BigDecimal value;

    @NotBlank(message = "Title is required")
    @Size(max = 60, message = "Title must not exceed 60 characters")
    private String title;
}