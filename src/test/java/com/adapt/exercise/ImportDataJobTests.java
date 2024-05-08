package com.adapt.exercise;

import com.adapt.exercise.job.jobBy.ImportDataJobConfig;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.sql.DataSource;


@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {ImportDataJobConfig.class, TestDatabaseConfig.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:/org/springframework/batch/core/schema-h2.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:/org/springframework/batch/core/schema-drop-h2.sql")
public class ImportDataJobTests {
    public static final Logger log = LoggerFactory.getLogger(ImportDataJobTests.class);
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(dataSource);
        log.info("Setup Job");
        jdbcTemplate.update("DELETE FROM ad_groups");
        jdbcTemplate.update("DELETE FROM campaigns");
        jdbcTemplate.update("DELETE FROM import_ad_groups");
        jdbcTemplate.update("DELETE FROM import_campaigns");
        jdbcTemplate.update("DELETE FROM accounts");
    }

    @AfterEach
    public void tearDown() {
        log.info("Tear down");
        jdbcTemplate.update("DELETE FROM ad_groups");
        jdbcTemplate.update("DELETE FROM campaigns");
        jdbcTemplate.update("DELETE FROM import_ad_groups");
        jdbcTemplate.update("DELETE FROM import_campaigns");
        jdbcTemplate.update("DELETE FROM accounts");
    }

    @Test
    public void testImportDataJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("accountFileInput", "C:/Users/USER/IdeaProjects/exercise/input/test/account_test.csv")
                .addString("campaignFileInput", "C:/Users/USER/IdeaProjects/exercise/input/test/campaign_test.csv")
                .addString("adGroupFileInput", "C:/Users/USER/IdeaProjects/exercise/input/test/adGroup_test.csv")
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        int accountsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM accounts WHERE is_valid = true", Integer.class);
        int expectedAccountsCount = 3;
        Assert.assertEquals(expectedAccountsCount, accountsCount);

        int campaignsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM campaigns WHERE is_valid = true", Integer.class);
        int expectedCampaignsCount = 5;
        Assert.assertEquals(expectedCampaignsCount, campaignsCount);

        int adGroupsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ad_groups WHERE is_valid = true", Integer.class);
        int expectedAdGroupsCount = 5;
        Assert.assertEquals(expectedAdGroupsCount, adGroupsCount);
    }
}
