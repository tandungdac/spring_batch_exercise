package com.adapt.exercise.job.jobBy.validate;

import com.adapt.exercise.model.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class AccountValidator implements Validator<Account> {

    public static final Logger log = LoggerFactory.getLogger(AccountValidator.class);

    private static final int MAX_NAME_LENGTH = 20;

    @Override
    public void validate(Account account) throws ValidationException {
        if (account.getName().length() > MAX_NAME_LENGTH) {
            log.error("Account name length exceeds " + MAX_NAME_LENGTH + " characters");
            throw new ValidationException("Account name length exceeds " + MAX_NAME_LENGTH + " characters");
        }
    }
}
