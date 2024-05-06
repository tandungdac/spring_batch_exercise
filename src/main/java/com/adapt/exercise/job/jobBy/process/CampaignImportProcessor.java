package com.adapt.exercise.job.jobBy.process;

import com.adapt.exercise.model.dto.input.CampaignInput;
import com.adapt.exercise.model.entity.Campaign;
import com.adapt.exercise.repository.IAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class CampaignImportProcessor implements ItemProcessor<CampaignInput, Campaign> {
    public static final Logger log = LoggerFactory.getLogger(CampaignImportProcessor.class);

    private ValidatingItemProcessor<CampaignInput> validatingItemProcessor;

    public CampaignImportProcessor(ValidatingItemProcessor<CampaignInput> validatingItemProcessor) {
        this.validatingItemProcessor = validatingItemProcessor;
    }

    @Autowired
    private IAccountRepository accountRepository;

    @Override
    public Campaign process(CampaignInput item) throws Exception {
        CampaignInput validatedInput = validatingItemProcessor.process(item);
        if (validatedInput != null) {
            log.info("Processing CampaignInput: {}", item);
            Campaign campaign = new Campaign();
            campaign.setId(item.getId());
            campaign.setName(item.getName());
            campaign.setType(item.getType());
            campaign.setBudget(Float.parseFloat(item.getBudget()));
            campaign.setAccount(accountRepository.findById(item.getAccountId()).orElse(null));
            campaign.setIsValid(true);
//            log.info("Processed Campaign: {}", campaign);
            return campaign;
        }
        else {
            return null;
        }
    }
}
