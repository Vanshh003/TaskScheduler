package com.pers.taskScheduler.service.impl;

import com.pers.taskScheduler.dto.CreateTaskRequest;
import com.pers.taskScheduler.dto.UpdateTaskRequest;
import com.pers.taskScheduler.entity.Task;
import com.pers.taskScheduler.enums.ScheduleType;
import com.pers.taskScheduler.repository.TaskRepository;
import com.pers.taskScheduler.scheduler.SchedulerManager;
import com.pers.taskScheduler.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SchedulerManager schedulerManager;

    @Override
    public Task createTask(CreateTaskRequest request) {

        validateRequest(request.getScheduleType(),
                request.getCronExpression(),
                request.getIntervalInSeconds());

        Task task = Task.builder()
                .name(request.getName())
                .taskType(request.getTaskType())
                .scheduleType(request.getScheduleType())
                .cronExpression(request.getCronExpression())
                .intervalInSeconds(request.getIntervalInSeconds())
                .timezone(request.getTimezone())
                .actionUrl(request.getActionUrl())
                .payload(request.getPayload())
                .maxRetries(request.getMaxRetries())
                .backoffStrategy(request.getBackoffStrategy())
                .active(true)
                .build();

        Task savedTask = taskRepository.save(task);

        schedulerManager.scheduleTask(savedTask);

        return savedTask;
    }

    @Override
    public Task updateTask(Long taskId, UpdateTaskRequest request) {

        Task task = getTask(taskId);

        validateRequest(request.getScheduleType(),
                request.getCronExpression(),
                request.getIntervalInSeconds());

        task.setScheduleType(request.getScheduleType());
        task.setCronExpression(request.getCronExpression());
        task.setIntervalInSeconds(request.getIntervalInSeconds());
        task.setTimezone(request.getTimezone());
        task.setActionUrl(request.getActionUrl());
        task.setPayload(request.getPayload());
        task.setMaxRetries(request.getMaxRetries());
        task.setBackoffStrategy(request.getBackoffStrategy());
        task.setActive(request.getActive());

        Task updatedTask = taskRepository.save(task);

        schedulerManager.updateTask(updatedTask);

        return updatedTask;
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = getTask(taskId);
        schedulerManager.deleteTask(task);
        taskRepository.delete(task);
    }

    @Override
    public Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    private void validateRequest(ScheduleType scheduleType, String cron, Integer interval) {

        if (scheduleType == ScheduleType.CRON) {
            if (cron == null || cron.isBlank()) {
                throw new RuntimeException("Cron expression is required for CRON schedule");
            }
        }

        if (scheduleType == ScheduleType.INTERVAL) {
            if (interval == null || interval < 1) {
                throw new RuntimeException("intervalInSeconds must be >= 1");
            }
        }
    }
}
