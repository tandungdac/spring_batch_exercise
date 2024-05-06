package com.adapt.exercise.controller;

import com.adapt.exercise.model.response.BatchJobResponse;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/importDataExistJob")
public class ImportDataExistController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("importDataExistJob")
    private Job importDataExistJob;

    @GetMapping("/runJob")
    public ResponseEntity<BatchJobResponse> runJob() {
        BatchJobResponse response = new BatchJobResponse();
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(importDataExistJob, jobParameters);
            response.setJobName(jobExecution.getJobInstance().getJobName());
            response.setStatus(jobExecution.getStatus().toString());
            response.setExitStatus(jobExecution.getExitStatus().getExitCode());
            response.setStartTime(jobExecution.getStartTime());
            response.setEndTime(jobExecution.getEndTime());
            response.setDuration(jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime());
        } catch (Exception e) {
            response.setStatus("Failed");
            response.setErrorMessage(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
