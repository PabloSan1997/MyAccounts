package com.myaccounts.service.myaccountsservice.repositories;

import com.myaccounts.service.myaccountsservice.models.entities.FixedIncomeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixedIncomeRepository extends CrudRepository<FixedIncomeEntity, Long> {
    List<FixedIncomeEntity> findByPeriodId(Long periodId);
}