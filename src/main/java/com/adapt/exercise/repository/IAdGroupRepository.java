package com.adapt.exercise.repository;

import com.adapt.exercise.model.entity.AdGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface IAdGroupRepository extends JpaRepository<AdGroup, Long> {
    @Modifying
    @Query("UPDATE AdGroup a SET a.isValid = false WHERE a.campaign.id = :campaignId")
    @Transactional
    void deleteByCampaignId(@Param("campaignId") Long campaignId);
}
