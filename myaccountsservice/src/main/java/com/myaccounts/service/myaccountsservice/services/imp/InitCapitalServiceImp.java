package com.myaccounts.service.myaccountsservice.services.imp;

import com.myaccounts.service.myaccountsservice.models.dtos.InitCapitalDto;
import com.myaccounts.service.myaccountsservice.models.dtos.InitCapitalPatchDto;
import com.myaccounts.service.myaccountsservice.models.entities.InitCapitalEntity;
import com.myaccounts.service.myaccountsservice.models.entities.UserEntity;
import com.myaccounts.service.myaccountsservice.repositories.InitCapitalRepository;
import com.myaccounts.service.myaccountsservice.repositories.UserRepository;
import com.myaccounts.service.myaccountsservice.services.InitCapitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class InitCapitalServiceImp implements InitCapitalService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InitCapitalRepository initCapitalRepository;

    @Override
    public InitCapitalDto getInitCapital() {
        UserEntity user = getUserEntity();
        if (user.getInitCapital() == null) {
            InitCapitalEntity initCapital = InitCapitalEntity.builder()
                    .initValue(java.math.BigDecimal.ZERO)
                    .created(Instant.now())
                    .user(user)
                    .build();
            initCapital = initCapitalRepository.save(initCapital);
            user.setInitCapital(initCapital);
            userRepository.save(user);
            return InitCapitalDto.builder()
                    .initValue(initCapital.getInitValue())
                    .created(initCapital.getCreated())
                    .build();
        }
        return InitCapitalDto.builder()
                .initValue(user.getInitCapital().getInitValue())
                .created(user.getInitCapital().getCreated())
                .build();
    }

    @Override
    public InitCapitalDto patchInitCapital(InitCapitalPatchDto dto) {
        UserEntity user = getUserEntity();
        if (user.getInitCapital() == null) {
            InitCapitalEntity initCapital = InitCapitalEntity.builder()
                    .initValue(dto.getInitValue() != null ? dto.getInitValue() : java.math.BigDecimal.ZERO)
                    .created(Instant.now())
                    .user(user)
                    .build();
            initCapital = initCapitalRepository.save(initCapital);
            user.setInitCapital(initCapital);
            userRepository.save(user);
            return InitCapitalDto.builder()
                    .initValue(initCapital.getInitValue())
                    .created(initCapital.getCreated())
                    .build();
        }
        InitCapitalEntity initCapital = user.getInitCapital();
        if (dto.getInitValue() != null) {
            initCapital.setInitValue(dto.getInitValue());
            initCapital = initCapitalRepository.save(initCapital);
        }
        return InitCapitalDto.builder()
                .initValue(initCapital.getInitValue())
                .created(initCapital.getCreated())
                .build();
    }

    private UserEntity getUserEntity() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}