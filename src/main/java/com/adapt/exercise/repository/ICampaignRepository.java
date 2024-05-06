package com.adapt.exercise.repository;

import com.adapt.exercise.model.entity.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ICampaignRepository extends JpaRepository<Campaign, Long> {
    @Modifying
    @Query("UPDATE Campaign a SET a.isValid = false WHERE a.account.id = :accountId")
    @Transactional
    void deleteByAccountId(@Param("accountId") Long accountId);

    Page<Campaign> findByIsValidTrue(Pageable pageable);

}
