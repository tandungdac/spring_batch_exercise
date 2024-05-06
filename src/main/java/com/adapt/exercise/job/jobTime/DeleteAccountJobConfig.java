package com.adapt.exercise.job.jobTime;

import com.adapt.exercise.model.entity.Account;

import com.adapt.exercise.model.entity.Campaign;
import com.adapt.exercise.repository.IAccountRepository;
import com.adapt.exercise.repository.IAdGroupRepository;
import com.adapt.exercise.repository.ICampaignRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableBatchProcessing
public class DeleteAccountJobConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private ICampaignRepository campaignRepository;

    @Autowired
    private IAdGroupRepository adGroupRepository;

    @Bean
    public Job deleteExpiredAccountJob() {
        return jobBuilderFactory
                .get("deleteExpiredAccountJob")
                .incrementer(new RunIdIncrementer())
                .start(deleteExpiredAccountStep())
                .build();
    }

    @Bean
    public Step deleteExpiredAccountStep() {
        return stepBuilderFactory.get("deleteExpiredAccountStep")
                .tasklet((contribution, chunkContext) -> {
                    List<Account> expiredAccounts = accountRepository.findByIsExpired(false);
                    for (Account account : expiredAccounts) {
                        for (Campaign campaign : account.getCampaigns())
                        {
                            adGroupRepository.deleteByCampaignId(campaign.getId());
                        }
                        campaignRepository.deleteByAccountId(account.getId());
                        accountRepository.deleteById(account.getId());
                    }
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
