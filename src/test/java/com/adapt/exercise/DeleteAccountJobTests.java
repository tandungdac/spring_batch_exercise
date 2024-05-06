package com.adapt.exercise;

import com.adapt.exercise.job.jobTime.DeleteAccountJobConfig;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@SpringBatchTest
@EnableAutoConfiguration
@ConfigurationProperties(value = "application.properties")
@ContextConfiguration(classes = {AnnotationConfigContextLoader.class, DeleteAccountJobConfig.class})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:schema.sql", "classpath:/org/springframework/batch/core/schema-mysql.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:/org/springframework/batch/core/schema-drop-mysql.sql")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class DeleteAccountJobTests {
    public static final Logger log = LoggerFactory.getLogger(DeleteAccountJobTests.class);
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DataSource datasource;

    @BeforeEach
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(datasource);
        log.info("Setup Job");
        String sql = "INSERT INTO accounts (id, name, is_expired) VALUES\n" +
                "(1,'A', true),\n" +
                "(2,'B', false),\n" +
                "(3,'C', true)";
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
    }
}
