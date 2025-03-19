package dev.splityosis.sysengine.scheduling;

import org.bukkit.Bukkit;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds all scheduling data (timezone and tasks).
 * The Scheduler will read from this Schedule
 * to know when to trigger.
 */
public class Schedule {

    private ZoneId zoneId;

    private final List<DailyTask> dailyTasks = new ArrayList<>();
    private final List<WeeklyTask> weeklyTasks = new ArrayList<>();
    private final List<MonthlyTask> monthlyTasks = new ArrayList<>();
    private final List<DateTask> dateTasks = new ArrayList<>();

    public Schedule(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public Schedule() {
        this(ZoneId.of("UTC"));
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public List<DailyTask> getDailyTasks() {
        return dailyTasks;
    }

    public List<WeeklyTask> getWeeklyTasks() {
        return weeklyTasks;
    }

    public List<MonthlyTask> getMonthlyTasks() {
        return monthlyTasks;
    }

    public List<DateTask> getDateTasks() {
        return dateTasks;
    }

    public void addDaily(String data, LocalTime... times) {
        dailyTasks.add(new DailyTask(data, Arrays.stream(times).map(Schedule::normalize).collect(Collectors.toList())));
    }

    public void addWeekly(String data, DayOfWeek dayOfWeek, LocalTime... times) {
        weeklyTasks.add(new WeeklyTask(data, dayOfWeek, Arrays.stream(times).map(Schedule::normalize).collect(Collectors.toList())));
    }

    public void addMonthly(String data, int dayOfMonth, LocalTime... times) {
        monthlyTasks.add(new MonthlyTask(data, dayOfMonth, Arrays.stream(times).map(Schedule::normalize).collect(Collectors.toList())));
    }

    public void addDate(String data, LocalDate date, LocalTime... times) {
        dateTasks.add(new DateTask(data, date, Arrays.stream(times).map(Schedule::normalize).collect(Collectors.toList())));
    }

    public void merge(Schedule other) {
        if (other == null) return;

        if (!this.zoneId.equals(other.zoneId))
            Bukkit.getLogger().warning("Warning: Merging schedules with different time zones (" + this.zoneId + " vs " + other.zoneId + ")");

        this.dailyTasks.addAll(other.getDailyTasks());
        this.weeklyTasks.addAll(other.getWeeklyTasks());
        this.monthlyTasks.addAll(other.getMonthlyTasks());
        this.dateTasks.addAll(other.getDateTasks());
    }

    private static LocalTime normalize(LocalTime time) {
        return time.withSecond(0).withNano(0);
    }

    public static class DailyTask {

        private final String data;
        private final List<LocalTime> times;

        public DailyTask(String data, List<LocalTime> times) {
            this.data = data;
            this.times = times;
        }

        public String getData() {
            return data;
        }

        public List<LocalTime> getTimes() {
            return times;
        }
    }

    public static class WeeklyTask {

        private final DayOfWeek dayOfWeek;
        private final String data;
        private final List<LocalTime> times;

        public WeeklyTask(String data, DayOfWeek dayOfWeek, List<LocalTime> times) {
            this.dayOfWeek = dayOfWeek;
            this.data = data;
            this.times = times;
        }

        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }

        public String getData() {
            return data;
        }

        public List<LocalTime> getTimes() {
            return times;
        }
    }

    public static class MonthlyTask {

        private final int dayOfMonth;
        private final String data;
        private final List<LocalTime> times;

        public MonthlyTask(String data, int dayOfMonth, List<LocalTime> times) {
            this.dayOfMonth = dayOfMonth;
            this.data = data;
            this.times = times;
        }

        public int getDayOfMonth() {
            return dayOfMonth;
        }

        public String getData() {
            return data;
        }

        public List<LocalTime> getTimes() {
            return times;
        }
    }

    public static class DateTask {

        private final LocalDate date;
        private final String data;
        private final List<LocalTime> times;

        public DateTask(String data, LocalDate date, List<LocalTime> times) {
            this.date = date;
            this.data = data;
            this.times = times;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getData() {
            return data;
        }

        public List<LocalTime> getTimes() {
            return times;
        }
    }
}
