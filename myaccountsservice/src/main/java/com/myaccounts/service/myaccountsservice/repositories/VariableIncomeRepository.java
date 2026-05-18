package com.myaccounts.service.myaccountsservice.repositories;

import com.myaccounts.service.myaccountsservice.models.entities.VariableIncomeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariableIncomeRepository extends CrudRepository<VariableIncomeEntity, Long> {
    List<VariableIncomeEntity> findByPeriodId(Long periodId);
}