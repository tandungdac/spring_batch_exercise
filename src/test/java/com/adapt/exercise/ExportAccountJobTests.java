package com.adapt.exercise;

import com.adapt.exercise.job.jobExport.ExportAccountJobConfig;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;


@SpringBatchTest
@EnableAutoConfiguration
@ConfigurationProperties(value = "application.properties")
@ContextConfiguration(classes = {AnnotationConfigContextLoader.class, ExportAccountJobConfig.class})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:schema.sql", "classpath:/org/springframework/batch/core/schema-mysql.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:/org/springframework/batch/core/schema-drop-mysql.sql")
public class ExportAccountJobTests {
    public static final Logger log = LoggerFactory.getLogger(ExportAccountJobTests.class);
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
                "(2,'B', true),\n" +
                "(3,'C', true)";
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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testExportAccountJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("accountFileOutput", "C:/Users/USER/IdeaProjects/exercise/output/test/account_test.csv")
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        try (Stream<String> stream = Files.lines(Paths.get("output/account_test.csv"))) {
            final List<String> actual = stream.collect(Collectors.toList());
            assertThat(actual, contains(
                    "Id,Name,Budget",
                    "1,A,310000.0",
                    "2,B,150000.0",
                    "3,C,80000.0"
            ));
        }
    }
}
