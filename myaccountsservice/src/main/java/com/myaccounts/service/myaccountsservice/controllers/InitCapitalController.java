package com.myaccounts.service.myaccountsservice.controllers;

import com.myaccounts.service.myaccountsservice.models.dtos.InitCapitalDto;
import com.myaccounts.service.myaccountsservice.models.dtos.InitCapitalPatchDto;
import com.myaccounts.service.myaccountsservice.services.InitCapitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/initCapital")
public class InitCapitalController {

    @Autowired
    private InitCapitalService initCapitalService;

    @GetMapping
    public ResponseEntity<InitCapitalDto> getInitCapital() {
        return ResponseEntity.ok(initCapitalService.getInitCapital());
    }

    @PatchMapping
    public ResponseEntity<InitCapitalDto> patchInitCapital(@RequestBody InitCapitalPatchDto dto) {
        return ResponseEntity.ok(initCapitalService.patchInitCapital(dto));
    }
}