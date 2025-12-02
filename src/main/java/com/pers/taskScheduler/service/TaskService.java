package com.pers.taskScheduler.service;

import com.pers.taskScheduler.dto.CreateTaskRequest;
import com.pers.taskScheduler.dto.UpdateTaskRequest;
import com.pers.taskScheduler.entity.Task;

import java.util.List;

public interface TaskService {

    Task createTask(CreateTaskRequest request);

    Task updateTask(Long taskId, UpdateTaskRequest request);

    void deleteTask(Long taskId);

    Task getTask(Long taskId);

    List<Task> getAllTasks();
}
