package com.adapt.exercise;

import com.adapt.exercise.job.jobTime.DeleteAccountJobConfig;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {DeleteAccountJobConfig.class, TestDatabaseConfig.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:/org/springframework/batch/core/schema-h2.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:/org/springframework/batch/core/schema-drop-h2.sql")
public class DeleteAccountJobTests {
    public static final Logger log = LoggerFactory.getLogger(DeleteAccountJobTests.class);
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    PlatformTransactionManager transactionManager;

    @BeforeEach
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(dataSource);
        log.info("Setup Job");
        jdbcTemplate.update("DELETE FROM ad_groups");
        jdbcTemplate.update("DELETE FROM campaigns");
        jdbcTemplate.update("DELETE FROM accounts");
        String sql = "INSERT INTO accounts (id, name, is_expired) VALUES\n" +
                "(1,'A', true),\n" +
                "(2,'B', false),\n" +
                "(3,'C', false)";
        jdbcTemplate.execute(sql);
        sql = "INSERT INTO campaigns (id, name, budget, type, account_id) VALUES\n" +
                "(1,'Campaign 1',100000,'Display',1),\n" +
                "(2,'Campaign 2',150000,'Search',2),\n" +
                "(3,'Campaign 3',80000,'Video',3),\n" +
                "(4,'Campaign 4',120000,'Display',1),\n" +
                "(5,'Campaign 5',90000,'Social',1)";
        jdbcTemplate.execute(sql);
    }

    @AfterEach
    public void tearDown() {
        log.info("Tear down");
        jdbcTemplate.update("DELETE FROM ad_groups");
        jdbcTemplate.update("DELETE FROM campaigns");
        jdbcTemplate.update("DELETE FROM accounts");
    }

    @Test
    public void testDeleteAccountJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        int accountsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM accounts WHERE is_valid = true", Integer.class);
        int expectedAccountsCount = 1;
        Assert.assertEquals(expectedAccountsCount, accountsCount);

        int campaignsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM campaigns WHERE is_valid = true", Integer.class);
        int expectedCampaignsCount = 3;
        Assert.assertEquals(expectedCampaignsCount, campaignsCount);
    }
}
