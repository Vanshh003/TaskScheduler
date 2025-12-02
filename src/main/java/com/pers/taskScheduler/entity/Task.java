package com.pers.taskScheduler.entity;

import com.pers.taskScheduler.enums.BackoffStrategy;
import com.pers.taskScheduler.enums.ScheduleType;
import com.pers.taskScheduler.enums.TaskType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    private String cronExpression;

    private Integer intervalInSeconds;

    private String timezone;

    private String actionUrl;

    @Column(columnDefinition = "text")
    private String payload;

    private Integer maxRetries;

    @Enumerated(EnumType.STRING)
    private BackoffStrategy backoffStrategy;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (active == null) {
            active = true;
        }
        if (maxRetries == null) {
            maxRetries = 0;
        }
        if (backoffStrategy == null) {
            backoffStrategy = BackoffStrategy.NONE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

