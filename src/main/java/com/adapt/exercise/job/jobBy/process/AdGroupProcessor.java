package com.adapt.exercise.job.jobBy.process;

import com.adapt.exercise.model.dto.input.AdGroupInput;
import com.adapt.exercise.model.entity.AdGroup;
import com.adapt.exercise.repository.ICampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class AdGroupProcessor implements ItemProcessor<AdGroupInput, AdGroup> {
    public static final Logger log = LoggerFactory.getLogger(AdGroupProcessor.class);

    private ValidatingItemProcessor<AdGroupInput> validatingItemProcessor;

    public AdGroupProcessor(ValidatingItemProcessor<AdGroupInput> validatingItemProcessor) {
        this.validatingItemProcessor = validatingItemProcessor;
    }

    @Autowired
    private ICampaignRepository campaignRepository;

    @Override
    public AdGroup process(AdGroupInput item) throws Exception {
        AdGroupInput adGroupInput = validatingItemProcessor.process(item);
        if (adGroupInput != null) {
            log.info("Processing AdGroupInput: {}", item);
            AdGroup adGroup = new AdGroup();
            adGroup.setId(item.getId());
            adGroup.setName(item.getName());
            adGroup.setType(item.getType());
            adGroup.setStatus(item.getStatus());
            adGroup.setBudget(Float.parseFloat(item.getBudget()));
            adGroup.setClick(item.getClick());
            adGroup.setView(item.getView());
            adGroup.setCampaign(campaignRepository.findById(item.getCampaignId()).orElse(null));
            adGroup.setIsValid(true);
//            log.info("Processed AdGroup: {}", adGroup);
            return adGroup;
        }
        else {
            return null;
        }
    }
}
