package com.pers.taskScheduler.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.taskScheduler.entity.Task;
import com.pers.taskScheduler.entity.TaskExecutionLog;
import com.pers.taskScheduler.enums.TaskStatus;
import com.pers.taskScheduler.enums.TaskType;
import com.pers.taskScheduler.handler.EmailTaskHandler;
import com.pers.taskScheduler.handler.WebhookTaskHandler;
import com.pers.taskScheduler.repository.TaskExecutionLogRepository;
import com.pers.taskScheduler.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TaskExecutorJob implements Job {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskExecutionLogRepository logRepository;

    @Autowired
    private WebhookTaskHandler webhookTaskHandler;

    @Autowired
    private EmailTaskHandler emailTaskHandler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long taskId = context.getMergedJobDataMap().getLong("taskId");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new JobExecutionException("Task not found: " + taskId));

        LocalDateTime start = LocalDateTime.now();
        TaskExecutionLog.TaskExecutionLogBuilder logBuilder = TaskExecutionLog.builder()
                .task(task)
                .startTime(start)
                .retryAttempt(0);

        try {
            // MAIN DECISION POINT
            int httpStatusCode = 200;

            if (task.getTaskType() == TaskType.SEND_EMAIL) {
                executeEmailTask(task);
            } else if (task.getTaskType() == TaskType.CUSTOM_WEBHOOK) {
                httpStatusCode = executeWebhookTask(task);
            }


            LocalDateTime end = LocalDateTime.now();
            logBuilder
                    .status(TaskStatus.SUCCESS)
                    .endTime(end)
                    .httpStatusCode(httpStatusCode)
                    .durationMs(java.time.Duration.between(start, end).toMillis());

        } catch (Exception e) {
            LocalDateTime end = LocalDateTime.now();
            logBuilder
                    .status(TaskStatus.FAILED)
                    .httpStatusCode(500)
                    .errorMessage(e.getMessage())
                    .endTime(end)
                    .durationMs(java.time.Duration.between(start, end).toMillis());

            log.error("Task {} execution FAILED: {}", taskId, e.getMessage());
        }

        logRepository.save(logBuilder.build());
    }

    private void executeEmailTask(Task task) throws Exception {
        emailTaskHandler.executeEmail(task.getPayload());
    }

    private int executeWebhookTask(Task task) {
        if (task.getActionUrl() == null || task.getActionUrl().isEmpty()) {
            throw new RuntimeException("actionUrl is required for CUSTOM_WEBHOOK task");
        }

        return webhookTaskHandler.executeWebhook(
                task.getActionUrl(),
                task.getPayload()
        );
    }


}
