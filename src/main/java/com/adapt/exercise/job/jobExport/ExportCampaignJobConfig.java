package com.adapt.exercise.job.jobExport;

import com.adapt.exercise.model.dto.output.CampaignOutput;
import com.adapt.exercise.job.jobExport.process.CampaignExportProcessor;
import com.adapt.exercise.model.entity.Campaign;
import com.adapt.exercise.repository.ICampaignRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
public class ExportCampaignJobConfig {
    @Value("${campaign.file.output}")
    String campaignFileOutput;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ICampaignRepository campaignRepository;

    @Bean
    public Job exportCampaignJob() {
        return jobBuilderFactory
                .get("exportCampaignJob")
                .incrementer(new RunIdIncrementer())
                .start(exportCampaignStep())
                .build();
    }

    @Bean
    public Step exportCampaignStep() {
        return stepBuilderFactory
                .get("exportCampaignStep")
                .<Campaign, CampaignOutput>chunk(10)
                .reader(campaignRepositoryItemReader())
                .processor(campaignExportProcessor())
                .writer(campaignFlatFileItemWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Campaign> campaignRepositoryItemReader() {
        HashMap<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        return new RepositoryItemReaderBuilder<Campaign>()
                .name("campaignRepositoryItemReader")
                .repository(campaignRepository)
                .methodName("findByIsValidTrue")
                .pageSize(20)
                .sorts(sorts)
                .build();
    }

    @Bean
    public CampaignExportProcessor campaignExportProcessor() {
        return new CampaignExportProcessor();
    }

    @Bean
    public FlatFileItemWriter<CampaignOutput> campaignFlatFileItemWriter() {
        return new FlatFileItemWriterBuilder<CampaignOutput>()
                .name("campaignFlatFileItemWriter")
                .resource(new FileSystemResource(campaignFileOutput))
                .delimited()
                .names("Id", "Name", "Budget", "Type", "AdGroupBudget", "AdGroupClick", "AdGroupView")
                .headerCallback(headerCampaignWriter())
                .build();
    }

    @Bean
    public FlatFileHeaderCallback headerCampaignWriter() {
        return writer -> writer.write("Id,Name,Budget,Type,Total Ad Group Budget,Total Ad Group Click,Total Ad Group View");
    }
}
