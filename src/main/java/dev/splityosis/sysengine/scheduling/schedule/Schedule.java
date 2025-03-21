package dev.splityosis.sysengine.scheduling.schedule;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Holds all scheduling data (timezone and tasks).
 * The Scheduler will read from this Schedule
 * to know when to trigger.
 */
public class Schedule {

    private ZoneId zoneId;

    private final List<DailyTaskSignature> dailyTaskSignatures = new CopyOnWriteArrayList<>();
    private final List<WeeklyTaskSignature> weeklyTaskSignatures = new CopyOnWriteArrayList<>();
    private final List<MonthlyTaskSignature> monthlyTaskSignatures = new CopyOnWriteArrayList<>();
    private final List<DateTaskSignature> dateTaskSignatures = new CopyOnWriteArrayList<>();

    public Schedule() {
        this.zoneId = ZoneId.systemDefault();
    }

    public Schedule(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return The time zone used for this schedule.
     */
    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * Sets the time zone for this schedule.
     * @param zoneId The time zone to set.
     */
    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public List<DailyTaskSignature> getDailyTaskSignatures() {
        return dailyTaskSignatures;
    }

    public List<WeeklyTaskSignature> getWeeklyTaskSignatures() {
        return weeklyTaskSignatures;
    }

    public List<MonthlyTaskSignature> getMonthlyTaskSignatures() {
        return monthlyTaskSignatures;
    }

    public List<DateTaskSignature> getDateTaskSignatures() {
        return dateTaskSignatures;
    }

    /**
     * Adds a daily task at specified times.
     * @param taskIdentifier Task taskIdentifier.
     * @param times Times to trigger each day.
     * @return This schedule.
     */
    public Schedule addDaily(@Nullable String taskIdentifier, LocalTime... times) {
        dailyTaskSignatures.add(new DailyTaskSignature(taskIdentifier, Arrays.stream(times).map(Schedule::normalize).collect(Collectors.toList())));
        return this;
    }

    /**
     * Adds a weekly task on a specific day and times.
     * @param taskIdentifier Task taskIdentifier.
     * @param dayOfWeek Day of the week to trigger.
     * @param times Times to trigger on that day.
     * @return This schedule.
     */
    public Schedule addWeekly(@Nullable String taskIdentifier, DayOfWeek dayOfWeek, LocalTime... times) {
        weeklyTaskSignatures.add(new WeeklyTaskSignature(taskIdentifier, dayOfWeek, Arrays.stream(times).map(Schedule::normalize).collect(Collectors.toList())));
        return this;
    }

    /**
     * Adds a monthly task on a specific day and times.
     * @param taskIdentifier Task taskIdentifier.
     * @param dayOfMonth Day of the month to trigger.
     * @param times Times to trigger on that day.
     * @return This schedule.
     */
    public Schedule addMonthly(@Nullable String taskIdentifier, int dayOfMonth, LocalTime... times) {
        monthlyTaskSignatures.add(new MonthlyTaskSignature(taskIdentifier, dayOfMonth, Arrays.stream(times).map(Schedule::normalize).collect(Collectors.toList())));
        return this;
    }

    /**
     * Adds a date-specific task at specified times.
     * @param taskIdentifier Task taskIdentifier.
     * @param date The date to trigger.
     * @param times Times to trigger on that date.
     * @return This schedule.
     */
    public Schedule addDate(@Nullable String taskIdentifier, LocalDate date, LocalTime... times) {
        dateTaskSignatures.add(new DateTaskSignature(taskIdentifier, date, Arrays.stream(times).map(Schedule::normalize).collect(Collectors.toList())));
        return this;
    }

    public Schedule clear() {
        dailyTaskSignatures.clear();
        weeklyTaskSignatures.clear();
        monthlyTaskSignatures.clear();
        dateTaskSignatures.clear();
        return this;
    }

    public Schedule merge(Schedule other) {
        if (other == null) return this;

        if (!this.zoneId.equals(other.zoneId))
            Bukkit.getLogger().warning("Warning: Merging schedules with different time zones (" + this.zoneId + " vs " + other.zoneId + ")");

        this.dailyTaskSignatures.addAll(other.getDailyTaskSignatures());
        this.weeklyTaskSignatures.addAll(other.getWeeklyTaskSignatures());
        this.monthlyTaskSignatures.addAll(other.getMonthlyTaskSignatures());
        this.dateTaskSignatures.addAll(other.getDateTaskSignatures());
        return this;
    }

    private static LocalTime normalize(LocalTime time) {
        return time.withSecond(0).withNano(0);
    }

    public static class DailyTaskSignature {

        private final String identifier;
        private final List<LocalTime> times;

        public DailyTaskSignature(String identifier, List<LocalTime> times) {
            this.identifier = identifier;
            this.times = times;
        }

        public String getIdentifier() {
            return identifier;
        }

        public List<LocalTime> getTimes() {
            return times;
        }

        @Override
        public String toString() {
            return "DailyTaskSignature{" +
                    "identifier='" + identifier + '\'' +
                    ", times=" + times +
                    '}';
        }
    }

    public static class WeeklyTaskSignature {

        private final DayOfWeek dayOfWeek;
        private final String identifier;
        private final List<LocalTime> times;

        public WeeklyTaskSignature(String identifier, DayOfWeek dayOfWeek, List<LocalTime> times) {
            this.dayOfWeek = dayOfWeek;
            this.identifier = identifier;
            this.times = times;
        }

        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }

        public String getIdentifier() {
            return identifier;
        }

        public List<LocalTime> getTimes() {
            return times;
        }

        @Override
        public String toString() {
            return "WeeklyTaskSignature{" +
                    "dayOfWeek=" + dayOfWeek +
                    ", identifier='" + identifier + '\'' +
                    ", times=" + times +
                    '}';
        }
    }

    public static class MonthlyTaskSignature {

        private final int dayOfMonth;
        private final String identifier;
        private final List<LocalTime> times;

        public MonthlyTaskSignature(String identifier, int dayOfMonth, List<LocalTime> times) {
            this.dayOfMonth = dayOfMonth;
            this.identifier = identifier;
            this.times = times;
        }

        public int getDayOfMonth() {
            return dayOfMonth;
        }

        public String getIdentifier() {
            return identifier;
        }

        public List<LocalTime> getTimes() {
            return times;
        }

        @Override
        public String toString() {
            return "MonthlyTaskSignature{" +
                    "times=" + times +
                    ", identifier='" + identifier + '\'' +
                    ", dayOfMonth=" + dayOfMonth +
                    '}';
        }
    }

    public static class DateTaskSignature {

        private final LocalDate date;
        private final String identifier;
        private final List<LocalTime> times;

        public DateTaskSignature(String identifier, LocalDate date, List<LocalTime> times) {
            this.date = date;
            this.identifier = identifier;
            this.times = times;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getIdentifier() {
            return identifier;
        }

        public List<LocalTime> getTimes() {
            return times;
        }

        @Override
        public String toString() {
            return "DateTaskSignature{" +
                    "date=" + date +
                    ", identifier='" + identifier + '\'' +
                    ", times=" + times +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "zoneId=" + zoneId +
                ", dailyTaskSignatures=" + dailyTaskSignatures +
                ", weeklyTaskSignatures=" + weeklyTaskSignatures +
                ", monthlyTaskSignatures=" + monthlyTaskSignatures +
                ", dateTaskSignatures=" + dateTaskSignatures +
                '}';
    }
}
