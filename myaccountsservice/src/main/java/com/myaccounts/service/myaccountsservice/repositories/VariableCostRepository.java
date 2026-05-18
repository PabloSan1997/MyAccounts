package com.myaccounts.service.myaccountsservice.repositories;

import com.myaccounts.service.myaccountsservice.models.entities.VariableCostEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariableCostRepository extends CrudRepository<VariableCostEntity, Long> {
    List<VariableCostEntity> findByPeriodId(Long periodId);
}