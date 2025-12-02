package com.pers.taskScheduler.repository;

import com.pers.taskScheduler.entity.Task;
import com.pers.taskScheduler.entity.TaskExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskExecutionLogRepository extends JpaRepository<TaskExecutionLog, Long> {

    Page<TaskExecutionLog> findByTask(Task task, Pageable pageable);
}

