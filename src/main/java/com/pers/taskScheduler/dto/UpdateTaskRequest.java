package com.pers.taskScheduler.dto;

import com.pers.taskScheduler.enums.BackoffStrategy;
import com.pers.taskScheduler.enums.ScheduleType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequest {

    @NotNull
    private ScheduleType scheduleType;

    private String cronExpression;

    private Integer intervalInSeconds;

    private String timezone;

    private String actionUrl;

    private String payload;

    private Integer maxRetries;

    private BackoffStrategy backoffStrategy;

    private Boolean active;
}
