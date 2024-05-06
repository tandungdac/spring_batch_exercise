package com.adapt.exercise.job.jobBy.process;

import com.adapt.exercise.model.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class AccountProcessor implements ItemProcessor<Account, Account> {
    public static final Logger log = LoggerFactory.getLogger(AccountProcessor.class);
    @Override
    public Account process(Account item) throws Exception {
        log.info("Processing Account: {}", item);
        item.setIsValid(true);
        return item;
    }
}
