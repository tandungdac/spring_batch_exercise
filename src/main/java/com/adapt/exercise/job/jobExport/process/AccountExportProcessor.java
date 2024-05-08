package com.adapt.exercise.job.jobExport.process;

import com.adapt.exercise.model.dto.output.AccountOutput;
import com.adapt.exercise.model.entity.Account;
import com.adapt.exercise.model.entity.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class AccountExportProcessor implements ItemProcessor<Account, AccountOutput> {

    public static final Logger log = LoggerFactory.getLogger(AccountExportProcessor.class);

    @Override
    public AccountOutput process(Account item) throws Exception {
        AccountOutput accountExport = new AccountOutput();
        accountExport.setId(item.getId());
        accountExport.setName(item.getName());

        float budget = 0;

        for(Campaign campaign : item.getCampaigns())
        {
            budget += campaign.getBudget();
        }

        accountExport.setBudget(budget);
        log.info("Processing Account: {}", accountExport);
        return accountExport;
    }
}
