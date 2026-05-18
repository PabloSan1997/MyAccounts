package com.myaccounts.service.myaccountsservice.repositories;

import com.myaccounts.service.myaccountsservice.models.entities.PeriodEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodRepository extends CrudRepository<PeriodEntity, Long> {
    List<PeriodEntity> findByUserId(Long userId);
    Optional<PeriodEntity> findByIdAndUserId(Long id, Long userId);
}