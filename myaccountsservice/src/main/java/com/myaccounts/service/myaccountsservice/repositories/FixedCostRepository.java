package com.myaccounts.service.myaccountsservice.repositories;

import com.myaccounts.service.myaccountsservice.models.entities.FixedCostEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixedCostRepository extends CrudRepository<FixedCostEntity, Long> {
    List<FixedCostEntity> findByPeriodId(Long periodId);
}