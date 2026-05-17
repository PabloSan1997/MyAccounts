package com.myaccounts.service.myaccountsservice.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {
    @Size(min = 1, max = 50)
    @NotBlank
    private String username;

    @Size(min = 1, max = 250)
    @NotBlank
    private String password;

    @Size(min = 1, max = 60)
    @NotBlank
    private String nickname;
}