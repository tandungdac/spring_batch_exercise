package com.adapt.exercise;

import com.adapt.exercise.repository.IAccountRepository;
import com.adapt.exercise.repository.IAdGroupRepository;
import com.adapt.exercise.repository.ICampaignRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class TestConfig {


    @MockBean
    private IAccountRepository accountRepository;

    @MockBean
    private ICampaignRepository campaignRepository;

    @MockBean
    private IAdGroupRepository adGroupRepository;

}