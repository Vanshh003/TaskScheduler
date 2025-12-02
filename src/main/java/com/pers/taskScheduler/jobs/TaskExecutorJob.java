package com.pers.taskScheduler.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.taskScheduler.entity.Task;
import com.pers.taskScheduler.entity.TaskExecutionLog;
import com.pers.taskScheduler.enums.TaskStatus;
import com.pers.taskScheduler.enums.TaskType;
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
            if (task.getTaskType() == TaskType.SEND_EMAIL) {
                executeEmailTask(task);
            } else if (task.getTaskType() == TaskType.CUSTOM_WEBHOOK) {
                executeWebhookTask(task);
            }

            LocalDateTime end = LocalDateTime.now();
            logBuilder
                    .status(TaskStatus.SUCCESS)
                    .endTime(end)
                    .durationMs(java.time.Duration.between(start, end).toMillis());

        } catch (Exception e) {
            LocalDateTime end = LocalDateTime.now();
            logBuilder
                    .status(TaskStatus.FAILED)
                    .errorMessage(e.getMessage())
                    .endTime(end)
                    .durationMs(java.time.Duration.between(start, end).toMillis());

            log.error("Task {} execution FAILED: {}", taskId, e.getMessage());
        }

        logRepository.save(logBuilder.build());
    }

    private void executeEmailTask(Task task) {
        // TODO: Implement email logic later
        log.info("Executing SEND_EMAIL task {}", task.getId());
    }

    private void executeWebhookTask(Task task) throws Exception {
        log.info("Executing CUSTOM_WEBHOOK task {}", task.getId());

        String url = task.getActionUrl();
        if (url == null) throw new RuntimeException("Webhook URL missing for task " + task.getId());

        // For now just simulate webhook execution
        log.info("Calling external URL: {}", url);
    }
}
