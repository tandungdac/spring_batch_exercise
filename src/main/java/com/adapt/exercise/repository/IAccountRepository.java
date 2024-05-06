package com.adapt.exercise.repository;

import com.adapt.exercise.model.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IAccountRepository extends JpaRepository<Account,Long> {
    List<Account> findByIsExpired(Boolean isExpired);

    Page<Account> findByIsValidTrue(Pageable pageable);

    @Modifying
    @Query("UPDATE Account a SET a.isValid = false WHERE a.id = :accountId")
    @Transactional
    void deleteById(@Param("accountId") Long accountId);
}
