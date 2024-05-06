package com.adapt.exercise.job.jobBy.validate;

import com.adapt.exercise.model.dto.input.AdGroupInput;
import com.adapt.exercise.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class AdGroupValidator implements Validator<AdGroupInput> {

    public static final Logger log = LoggerFactory.getLogger(AdGroupValidator.class);
    private static final int MAX_NAME_LENGTH = 99;

    @Override
    public void validate(AdGroupInput adGroup) throws ValidationException {
        if (adGroup.getName().length() > MAX_NAME_LENGTH) {
            log.error("Ad group name length exceeds " + MAX_NAME_LENGTH + " characters");
            throw new ValidationException("Ad group name length exceeds " + MAX_NAME_LENGTH + " characters");
        }

        if (adGroup.getBudget() == null || !ValidationUtil.isFloat(adGroup.getBudget())) {
            log.error("Budget must be a float value");
            throw new ValidationException("Budget must be a float value");
        }
    }
}
