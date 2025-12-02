package com.pers.taskScheduler.repository;

import com.pers.taskScheduler.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}

