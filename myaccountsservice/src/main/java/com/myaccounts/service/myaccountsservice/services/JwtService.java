package com.myaccounts.service.myaccountsservice.services;

import com.myaccounts.service.myaccountsservice.models.dtos.LoginClaimsDto;
import com.myaccounts.service.myaccountsservice.models.dtos.UserDetailsDto;

public interface JwtService {
    String accessToken(UserDetailsDto userDetailsDto);
    UserDetailsDto validationAccessToken(String token);
    String loginToken(com.myaccounts.service.myaccountsservice.models.entities.UserEntity user);
    LoginClaimsDto validationLoginToken(String token);
    void logout(String token);
}