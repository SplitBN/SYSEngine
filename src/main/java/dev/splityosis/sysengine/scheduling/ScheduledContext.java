package dev.splityosis.sysengine.scheduling;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Provides context about the triggered event.
 */
public class ScheduledContext {
    private final TaskType taskType;
    private final String data;
    private final LocalDate date;
    private final LocalTime time;
    private final DayOfWeek dayOfWeek;

    public ScheduledContext(TaskType taskType, String data, LocalDate date, LocalTime time, DayOfWeek dayOfWeek) {
        this.taskType = taskType;
        this.data = data;
        this.date = date;
        this.time = time;
        this.dayOfWeek = dayOfWeek;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getData() {
        return data;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
}