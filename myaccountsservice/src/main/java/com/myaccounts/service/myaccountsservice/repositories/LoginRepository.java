package com.myaccounts.service.myaccountsservice.repositories;

import com.myaccounts.service.myaccountsservice.models.entities.LoginEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends CrudRepository<LoginEntity, Long> {
    @Query("select u from LoginEntity u where u.id=:id and u.user.username=:username")
    Optional<LoginEntity> findByIdAndUsername(@Param("id") Long id, @Param("username") String username);
}