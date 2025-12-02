package com.pers.taskScheduler.scheduler;

import com.pers.taskScheduler.entity.Task;
import com.pers.taskScheduler.enums.ScheduleType;
import com.pers.taskScheduler.jobs.TaskExecutorJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulerManager {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    public void scheduleTask(Task task) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = buildJobDetail(task);
            Trigger trigger = buildTrigger(task);

            scheduler.scheduleJob(jobDetail, trigger);

            log.info("Scheduled task {} with id {}", task.getName(), task.getId());
        } catch (SchedulerException e) {
            log.error("Error scheduling task {}", task.getId(), e);
            throw new RuntimeException(e);
        }
    }

    public void updateTask(Task task) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            TriggerKey triggerKey = new TriggerKey("trigger-" + task.getId(), "task-triggers");

            Trigger newTrigger = buildTrigger(task);

            scheduler.rescheduleJob(triggerKey, newTrigger);

            log.info("Updated task {}", task.getId());
        } catch (SchedulerException e) {
            log.error("Error updating task {}", task.getId(), e);
            throw new RuntimeException(e);
        }
    }

    public void deleteTask(Task task) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobKey jobKey = new JobKey("task-" + task.getId(), "task-jobs");

            scheduler.deleteJob(jobKey);

            log.info("Deleted task {}", task.getId());
        } catch (SchedulerException e) {
            log.error("Error deleting task {}", task.getId(), e);
            throw new RuntimeException(e);
        }
    }

    private JobDetail buildJobDetail(Task task) {
        JobDataMap jobData = new JobDataMap();
        jobData.put("taskId", task.getId());

        return JobBuilder.newJob(TaskExecutorJob.class)
                .withIdentity("task-" + task.getId(), "task-jobs")
                .usingJobData(jobData)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(Task task) {
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity("trigger-" + task.getId(), "task-triggers");

        if (task.getScheduleType() == ScheduleType.CRON) {
            return builder
                    .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression()))
                    .build();
        } else {
            return builder
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(task.getIntervalInSeconds())
                            .repeatForever())
                    .build();
        }
    }
}
