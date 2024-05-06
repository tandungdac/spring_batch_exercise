package com.adapt.exercise.job.jobExport;

import com.adapt.exercise.model.dto.output.AccountOutput;
import com.adapt.exercise.job.jobExport.process.AccountExportProcessor;
import com.adapt.exercise.model.entity.Account;
import com.adapt.exercise.repository.IAccountRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;

import java.util.HashMap;

@Configuration
@EnableBatchProcessing
public class ExportAccountJobConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private IAccountRepository accountRepository;

    @Bean
    public Job exportAccountJob(
            Step exportAccountStep
    ) {
        return jobBuilderFactory
                .get("exportAccountJob")
                .incrementer(new RunIdIncrementer())
                .start(exportAccountStep)
                .build();
    }

    @Bean
    public Step exportAccountStep(
            FlatFileItemWriter<AccountOutput> accountExportFlatFileItemWriter
    ) {
        return stepBuilderFactory
                .get("exportAccountStep")
                .<Account, AccountOutput>chunk(10)
                .reader(accountRepositoryItemReader())
                .processor(accountExportProcessor())
                .writer(accountExportFlatFileItemWriter)
                .build();
    }

    @Bean
    public RepositoryItemReader<Account> accountRepositoryItemReader() {
        HashMap<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        return new RepositoryItemReaderBuilder<Account>()
                .name("accountRepositoryItemReader")
                .repository(accountRepository)
                .methodName("findByIsValidTrue")
                .pageSize(20)
                .sorts(sorts)
                .build();
    }

    @Bean
    public AccountExportProcessor accountExportProcessor() {
        return new AccountExportProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<AccountOutput> accountExportFlatFileItemWriter(
            @Value("#{jobParameters['accountFileOutput']}") String output
    ) {
        return new FlatFileItemWriterBuilder<AccountOutput>()
                .name("accountExportFlatFileItemWriter")
                .resource(new FileSystemResource(output))
                .delimited()
                .names("Id", "Name", "Budget")
                .headerCallback(headerAccountWriter())
                .build();
    }

    @Bean
    public FlatFileHeaderCallback headerAccountWriter() {
        return writer -> writer.write("Id,Name,Budget");
    }
}
