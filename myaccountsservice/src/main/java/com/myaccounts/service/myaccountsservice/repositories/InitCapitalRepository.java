package com.myaccounts.service.myaccountsservice.repositories;

import com.myaccounts.service.myaccountsservice.models.entities.InitCapitalEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InitCapitalRepository extends CrudRepository<InitCapitalEntity, Long> {
    Optional<InitCapitalEntity> findByUserId(Long userId);
}