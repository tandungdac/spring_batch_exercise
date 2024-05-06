package com.adapt.exercise;

import com.adapt.exercise.job.jobBy.ImportDataJobConfig;
import org.junit.Assert;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBatchTest
@EnableAutoConfiguration
@ConfigurationProperties(value = "application.properties")
@ContextConfiguration(classes = {AnnotationConfigContextLoader.class, ImportDataJobConfig.class})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:schema.sql", "classpath:/org/springframework/batch/core/schema-mysql.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:/org/springframework/batch/core/schema-drop-mysql.sql")
public class ImportDataJobTests {
    public static final Logger log = LoggerFactory.getLogger(ImportDataJobTests.class);
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testImportDataJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("accountFileInput", "C:/Users/USER/IdeaProjects/exercise/input/test/account_test.csv")
                .addString("campaignFileInput", "C:/Users/USER/IdeaProjects/exercise/input/test/campaign_test.csv")
                .addString("adGroupFileInput", "C:/Users/USER/IdeaProjects/exercise/input/test/adGroup_test.csv")
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
}
