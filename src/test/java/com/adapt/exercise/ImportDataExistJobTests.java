package com.adapt.exercise;

import com.adapt.exercise.job.jobImport.ImportDataExistJobConfig;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {ImportDataExistJobConfig.class, TestDatabaseConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:/org/springframework/batch/core/schema-h2.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:/org/springframework/batch/core/schema-drop-h2.sql")
public class ImportDataExistJobTests {

    public static final Logger log = LoggerFactory.getLogger(ImportDataExistJobTests.class);

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
        String sql = "INSERT INTO accounts (id, name, is_expired) VALUES\n" +
                "(1,'A', true),\n" +
                "(2,'B', true),\n" +
                "(3,'C', true),\n" +
                "(4,'D', true),\n" +
                "(5,'E', true)";
        jdbcTemplate.execute(sql);
        sql = "INSERT INTO campaigns (id, name, budget, type, account_id) VALUES\n" +
                "(1,'Campaign 1',100000,'Display',1),\n" +
                "(2,'Campaign 2',150000,'Search',2),\n" +
                "(3,'Campaign 3',80000,'Video',3),\n" +
                "(4,'Campaign 4',120000,'Display',4),\n" +
                "(5,'Campaign 5',120000,'Social',5),\n" +
                "(6,'Campaign 6',120000,'PPC',1),\n" +
                "(7,'Campaign 7',120000,'Display',2),\n" +
                "(8,'Campaign 8',120000,'Email',3),\n" +
                "(9,'Campaign 9',120000,'Social',4),\n" +
                "(10,'Campaign 10',90000,'SEO',5)";
        jdbcTemplate.execute(sql);

        sql = "INSERT INTO ad_groups (id, name, budget, type, status, click, view, campaign_id) VALUES\n" +
                "(1, 'Advertising Campaign A', 1000.0, 'Display', 'Active', 500, 10000, 1),\n" +
                "(2, 'Marketing Campaign B', 2000.0, 'Social', 'Inactive', 300, 8000, 1),\n" +
                "(3, 'Email Campaign C', 1500.0, 'Email', 'Active', 700, 12000, 2),\n" +
                "(4, 'SEO Campaign D', 1800.0, 'SEO', 'Active', 600, 9500, 2),\n" +
                "(5, 'PPC Campaign E', 2500.0, 'PPC', 'Inactive', 400, 7500, 3),\n" +
                "(6, 'Content Campaign F', 2200.0, 'Content', 'Active', 550, 10500, 4),\n" +
                "(7, 'Social Media Campaign G', 1900.0, 'Social Media', 'Inactive', 350, 8200, 5),\n" +
                "(8, 'Display Campaign H', 2800.0, 'Display', 'Active', 450, 8500, 6),\n" +
                "(9, 'Email Campaign I', 1700.0, 'Email', 'Inactive', 650, 9000, 7),\n" +
                "(10, 'SEO Campaign J', 3000.0, 'SEO', 'Active', 700, 11000, 8),\n" +
                "(11, 'PPC Campaign K', 2300.0, 'PPC', 'Inactive', 500, 8200, 9),\n" +
                "(12, 'Social Campaign L', 2100.0, 'Social', 'Active', 400, 7500, 10)";
        jdbcTemplate.execute(sql);
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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testExportAccountJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        int campaignsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM import_campaigns WHERE is_valid = true", Integer.class);
        int expectedCampaignsCount = 10;
        Assert.assertEquals(expectedCampaignsCount, campaignsCount);

        int adGroupsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM import_ad_groups WHERE is_valid = true", Integer.class);
        int expectedAdGroupsCount = 12;
        Assert.assertEquals(expectedAdGroupsCount, adGroupsCount);
    }
}
