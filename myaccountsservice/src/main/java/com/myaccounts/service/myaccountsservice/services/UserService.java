package com.myaccounts.service.myaccountsservice.services;

import com.myaccounts.service.myaccountsservice.models.dtos.DoubleJwtDto;
import com.myaccounts.service.myaccountsservice.models.dtos.JwtDto;
import com.myaccounts.service.myaccountsservice.models.dtos.LoginDto;
import com.myaccounts.service.myaccountsservice.models.dtos.RegisterDto;
import com.myaccounts.service.myaccountsservice.models.dtos.UserInfoDto;

public interface UserService{
    DoubleJwtDto register(RegisterDto registerDto);
    DoubleJwtDto login(LoginDto loginDto);
    UserInfoDto getUserInfo();
    void logout(String token);
    JwtDto refreshToken(String token);
}