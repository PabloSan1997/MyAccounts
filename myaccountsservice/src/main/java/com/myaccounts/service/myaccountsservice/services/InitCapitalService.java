package com.myaccounts.service.myaccountsservice.services;

import com.myaccounts.service.myaccountsservice.models.dtos.InitCapitalDto;
import com.myaccounts.service.myaccountsservice.models.dtos.InitCapitalPatchDto;

public interface InitCapitalService {
    InitCapitalDto getInitCapital();
    InitCapitalDto patchInitCapital(InitCapitalPatchDto dto);
}