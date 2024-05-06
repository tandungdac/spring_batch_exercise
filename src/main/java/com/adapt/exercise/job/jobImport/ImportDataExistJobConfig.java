package com.adapt.exercise.job.jobImport;

import com.adapt.exercise.job.jobBy.process.AdGroupProcessor;
import com.adapt.exercise.job.jobBy.process.CampaignImportProcessor;
import com.adapt.exercise.job.jobBy.validate.CampaignValidator;
import com.adapt.exercise.model.dto.input.AdGroupInput;
import com.adapt.exercise.model.dto.input.CampaignInput;
import com.adapt.exercise.model.entity.AdGroup;
import com.adapt.exercise.model.entity.Campaign;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.validation.ConstraintViolationException;
import java.io.File;

@Configuration
@EnableBatchProcessing
public class ImportDataExistJobConfig {

    public static final Logger log = LoggerFactory.getLogger(ImportDataExistJobConfig.class);
    @Value("${campaign.file.import}")
    public String campaignFileImport;

    @Value("${ad-group.file.import}")
    public String adGroupFileImport;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ValidatingItemProcessor<CampaignInput> validatingCampaignProcessor;

    @Autowired
    private ValidatingItemProcessor<AdGroupInput> validatingAdGroupProcessor;

    @Bean
    public Job importDataExistJob() {
        return jobBuilderFactory
                .get("importDataExistJob")
                .incrementer(new RunIdIncrementer())
                .start(importJsonCampaignStep())
                .next(importJsonAdGroupStep())
                .build();
    }

    @Bean
    public Step importJsonCampaignStep() {
        return stepBuilderFactory
                .get("importJsonCampaignStep")
                .<CampaignInput, CampaignInput>chunk(10)
                .reader(campaignInputJsonItemReader())
                .processor(validatingCampaignProcessor)
                .writer(campaignItemWriter())
                .faultTolerant()
                .skip(ConstraintViolationException.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Step importJsonAdGroupStep() {
        return stepBuilderFactory
                .get("importJsonAdGroupStep")
                .<AdGroupInput, AdGroupInput>chunk(10)
                .reader(adGroupInputJsonItemReader())
                .processor(validatingAdGroupProcessor)
                .writer(adGroupItemWriter())
                .faultTolerant()
                .skip(ConstraintViolationException.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public JsonItemReader<CampaignInput> campaignInputJsonItemReader() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonJsonObjectReader<CampaignInput> jsonObjectReader = new JacksonJsonObjectReader<>(CampaignInput.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<CampaignInput>()
                .jsonObjectReader(jsonObjectReader)
                .resource(new PathResource((new File(campaignFileImport)).getPath()))
                .name("campaignInputJsonItemReader")
                .build();
    }

    @Bean
    public JsonItemReader<AdGroupInput> adGroupInputJsonItemReader() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonJsonObjectReader<AdGroupInput> jsonObjectReader = new JacksonJsonObjectReader<>(AdGroupInput.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<AdGroupInput>()
                .jsonObjectReader(jsonObjectReader)
                .resource(new PathResource((new File(adGroupFileImport)).getPath()))
                .name("adGroupInputJsonItemReader")
                .build();
    }

    @Bean
    public ItemWriter<CampaignInput> campaignItemWriter() {
        return items -> {
            for (CampaignInput campaign : items) {
//                log.info("Campaign: " + campaign.toString());
                if (isCampaignExists(campaign.getName(), campaign.getAccountId()) && !isImportCampaignExists(campaign.getName(), campaign.getAccountId())) {
                    insertCampaign(campaign);
                }
            }
        };
    }

    @Bean
    public ItemWriter<AdGroupInput> adGroupItemWriter() {
        return items -> {
            for (AdGroupInput adGroup : items) {
//                log.info("Ad Group: " + adGroup.toString());
                if (isAdGroupExists(adGroup.getName(), adGroup.getCampaignId()) && !isImportAdGroupExists(adGroup.getName(), adGroup.getCampaignId())) {
                    insertAdGroup(adGroup);
                }
            }
        };
    }

    private boolean isCampaignExists(String campaignName, Long accountId) {
        String sql = "SELECT COUNT(*) FROM campaigns WHERE name = ? AND account_id = ? AND is_valid = true";
        return jdbcTemplate.queryForObject(sql, Integer.class, campaignName, accountId) > 0;
    }

    private boolean isImportCampaignExists(String campaignName, Long accountId) {
        String sql = "SELECT COUNT(*) FROM import_campaigns WHERE name = ? AND account_id = ? AND is_valid = true";
        return jdbcTemplate.queryForObject(sql, Integer.class, campaignName, accountId) > 0;
    }

    private void insertCampaign(CampaignInput campaign) {
        String sql = "INSERT INTO import_campaigns (id, name, budget, type, account_id, is_valid) VALUES (?, ?, ?, ?, ?, true)";
        jdbcTemplate.update(sql, campaign.getId(), campaign.getName(), campaign.getBudget(), campaign.getType(), campaign.getAccountId());
    }

    private boolean isImportAdGroupExists(String adGroupName, Long campaignId) {
        String sql = "SELECT COUNT(*) FROM import_ad_groups WHERE name = ? AND campaign_id = ? AND is_valid = true";
        return jdbcTemplate.queryForObject(sql, Integer.class, adGroupName, campaignId) > 0;
    }

    private boolean isAdGroupExists(String adGroupName, Long campaignId) {
        String sql = "SELECT COUNT(*) FROM ad_groups WHERE name = ? AND campaign_id = ? AND is_valid = true";
        return jdbcTemplate.queryForObject(sql, Integer.class, adGroupName, campaignId) > 0;
    }

    private void insertAdGroup(AdGroupInput adGroup) {
        String sql = "INSERT INTO import_ad_groups (id, name, budget, type, status, click, view, campaign_id, is_valid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, true)";
        jdbcTemplate.update(sql, adGroup.getId(), adGroup.getName(), adGroup.getBudget(), adGroup.getType(), adGroup.getStatus(), adGroup.getClick(), adGroup.getView(), adGroup.getCampaignId());
    }

}
