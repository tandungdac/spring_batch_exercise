package com.adapt.exercise.job.jobBy.validate;

import com.adapt.exercise.model.dto.input.CampaignInput;
import com.adapt.exercise.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class CampaignValidator implements Validator<CampaignInput> {
    public static final Logger log = LoggerFactory.getLogger(CampaignValidator.class);
    private static final int MAX_NAME_LENGTH = 99;

    @Override
    public void validate(CampaignInput campaign) throws ValidationException {
        if (campaign.getName().length() > MAX_NAME_LENGTH) {
            log.error("Campaign name length exceeds " + MAX_NAME_LENGTH + " characters");
            throw new ValidationException("Campaign name length exceeds " + MAX_NAME_LENGTH + " characters");
        }

        if (campaign.getBudget() == null || !ValidationUtil.isFloat(campaign.getBudget())) {
            log.error("Budget must be a float value");
            throw new ValidationException("Budget must be a float value");
        }
    }
}
