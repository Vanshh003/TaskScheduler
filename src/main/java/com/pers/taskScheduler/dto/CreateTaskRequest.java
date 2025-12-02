package com.pers.taskScheduler.dto;

import com.pers.taskScheduler.enums.BackoffStrategy;
import com.pers.taskScheduler.enums.ScheduleType;
import com.pers.taskScheduler.enums.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest {

    @NotBlank
    private String name;

    @NotNull
    private TaskType taskType;

    @NotNull
    private ScheduleType scheduleType;

    private String cronExpression;

    private Integer intervalInSeconds;

    private String timezone = "UTC"; // default

    private String actionUrl; // for webhook tasks

    private String payload;  // raw JSON string

    private Integer maxRetries = 0;

    private BackoffStrategy backoffStrategy = BackoffStrategy.NONE;
}
