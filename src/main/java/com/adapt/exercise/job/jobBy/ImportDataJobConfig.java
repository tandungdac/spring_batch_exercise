package com.adapt.exercise.job.jobBy;

import com.adapt.exercise.job.jobBy.process.AccountProcessor;
import com.adapt.exercise.job.jobBy.process.AdGroupProcessor;
import com.adapt.exercise.job.jobBy.process.CampaignImportProcessor;
import com.adapt.exercise.job.jobBy.validate.AccountValidator;
import com.adapt.exercise.job.jobBy.validate.AdGroupValidator;
import com.adapt.exercise.job.jobBy.validate.CampaignValidator;
import com.adapt.exercise.model.dto.input.AdGroupInput;
import com.adapt.exercise.model.dto.input.CampaignInput;
import com.adapt.exercise.model.entity.Account;
import com.adapt.exercise.model.entity.AdGroup;
import com.adapt.exercise.model.entity.Campaign;
import com.adapt.exercise.repository.IAccountRepository;
import com.adapt.exercise.repository.IAdGroupRepository;
import com.adapt.exercise.repository.ICampaignRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import javax.validation.ConstraintViolationException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class ImportDataJobConfig {
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
    public Job importDataJob(
            Step importAccountStep,
            Step importCampaignStep,
            Step importAdGroupStep
    ) {
        return jobBuilderFactory
                .get("importDataJob")
                .incrementer(new RunIdIncrementer())
                .start(importAccountStep)
                .next(importCampaignStep)
                .next(importAdGroupStep)
                .build();
    }

    @Bean
    public Step importAccountStep(
            FlatFileItemReader<Account> accountReader
    ) {
        return stepBuilderFactory
                .get("importAccountStep")
                .<Account, Account>chunk(10)
                .reader(accountReader)
                .processor(accountCompositeItemProcessor())
                .writer(accountRepositoryItemWriter())
                .faultTolerant()
                .skip(ConstraintViolationException.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Step importCampaignStep(
            FlatFileItemReader<CampaignInput> campaignReader
    ) {
        return stepBuilderFactory
                .get("importCampaignStep")
                .<CampaignInput, Campaign>chunk(10)
                .reader(campaignReader)
                .processor(campaignProcessor())
                .writer(campaignRepositoryItemWriter())
                .faultTolerant()
                .skip(ConstraintViolationException.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Step importAdGroupStep(
            FlatFileItemReader<AdGroupInput> adGroupReader
    ) {
        return stepBuilderFactory
                .get("importAdGroupStep")
                .<AdGroupInput, AdGroup>chunk(10)
                .reader(adGroupReader)
                .processor(adGroupProcessor())
                .writer(adGroupRepositoryItemWriter())
                .faultTolerant()
                .skip(ConstraintViolationException.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Account> accountReader(
            @Value("#{jobParameters['accountFileInput']}") String input
    ) {
        return new FlatFileItemReaderBuilder<Account>()
                .name("accountReader")
                .resource(new PathResource((new File(input)).getPath()))
                .delimited()
                .names("Id", "Name", "IsExpired")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Account>() {{
                    setTargetType(Account.class);
                }})
                .strict(true)
                .linesToSkip(1)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CampaignInput> campaignReader(
            @Value("#{jobParameters['campaignFileInput']}") String input
    ) {
        return new FlatFileItemReaderBuilder<CampaignInput>()
                .name("campaignReader")
                .resource(new PathResource((new File(input)).getPath()))
                .delimited()
                .names(new String[]{"Id", "Name", "Budget", "Type", "AccountId"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<CampaignInput>() {{
                    setTargetType(CampaignInput.class);
                }})
                .strict(true)
                .linesToSkip(1)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<AdGroupInput> adGroupReader(
            @Value("#{jobParameters['adGroupFileInput']}") String input
    ) {
        return new FlatFileItemReaderBuilder<AdGroupInput>()
                .name("adGroupReader")
                .resource(new PathResource((new File(input)).getPath()))
                .delimited()
                .names(new String[]{"Id", "Name", "Budget", "Type", "Status", "Click", "View", "CampaignId"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<AdGroupInput>() {{
                    setTargetType(AdGroupInput.class);
                }})
                .strict(true)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public AccountProcessor accountProcessor() {
        return new AccountProcessor();
    }

    @Bean
    public ValidatingItemProcessor<Account> validatingAccountProcessor() {
        ValidatingItemProcessor<Account> validatingItemProcessor = new ValidatingItemProcessor<>();
        validatingItemProcessor.setValidator(accountValidator());
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    @Bean
    public AccountValidator accountValidator() {
        return new AccountValidator();
    }

    @Bean
    public CompositeItemProcessor<Account, Account> accountCompositeItemProcessor() {
        List<ItemProcessor<Account, Account>> delegates = new ArrayList<>();
        delegates.add(validatingAccountProcessor());
        delegates.add(accountProcessor());

        CompositeItemProcessor<Account, Account> processor = new CompositeItemProcessor<>();
        processor.setDelegates(delegates);

        return processor;
    }

    @Bean
    public CampaignImportProcessor campaignProcessor() {
        return new CampaignImportProcessor(validatingCampaignProcessor());
    }

    @Bean
    public ValidatingItemProcessor<CampaignInput> validatingCampaignProcessor() {
        ValidatingItemProcessor<CampaignInput> validatingItemProcessor = new ValidatingItemProcessor<>();
        validatingItemProcessor.setValidator(campaignValidator());
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    @Bean
    public CampaignValidator campaignValidator() {
        return new CampaignValidator();
    }

    @Bean
    public AdGroupProcessor adGroupProcessor() {
        return new AdGroupProcessor(validatingAdGroupProcessor());
    }

    @Bean
    public ValidatingItemProcessor<AdGroupInput> validatingAdGroupProcessor() {
        ValidatingItemProcessor<AdGroupInput> validatingItemProcessor = new ValidatingItemProcessor<>();
        validatingItemProcessor.setValidator(adGroupValidator());
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    @Bean
    public AdGroupValidator adGroupValidator() {
        return new AdGroupValidator();
    }

    @Bean
    public RepositoryItemWriter<Account> accountRepositoryItemWriter() {
        RepositoryItemWriter<Account> writer = new RepositoryItemWriter<>();
        writer.setRepository(accountRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public RepositoryItemWriter<Campaign> campaignRepositoryItemWriter() {
        RepositoryItemWriter<Campaign> writer = new RepositoryItemWriter<>();
        writer.setRepository(campaignRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public RepositoryItemWriter<AdGroup> adGroupRepositoryItemWriter() {
        RepositoryItemWriter<AdGroup> writer = new RepositoryItemWriter<>();
        writer.setRepository(adGroupRepository);
        writer.setMethodName("save");
        return writer;
    }
}
