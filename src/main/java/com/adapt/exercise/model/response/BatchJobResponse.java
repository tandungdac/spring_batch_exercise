package com.adapt.exercise.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BatchJobResponse {
    private String jobName;
    private String status;
    private String exitStatus;
    private Date startTime;
    private Date endTime;
    private long duration;
    private String errorMessage;
}
