package dev.splityosis.sysengine.scheduling;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;

public class SimpleScheduler implements Scheduler {

    private Schedule schedule;
    private MissedScheduleStrategy missedScheduleStrategy = MissedScheduleStrategy.NONE;
    private File dataFile;
    private Consumer<ScheduledContext> executionHandler;
    private boolean isEnabled = false;

    private FileConfiguration config = new YamlConfiguration();
    private BukkitRunnable runnable;

    @Override
    public Scheduler setSchedule(@Nullable Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    @Override
    public Schedule getSchedule() {
        return schedule;
    }

    @Override
    public Scheduler trigger(ScheduledContext context) {
        executionHandler.accept(context);
        return this;
    }

    @Override
    public Scheduler enable(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        isEnabled = true;

        if (runnable != null)
            runnable.cancel();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                check();

                if (missedScheduleStrategy != MissedScheduleStrategy.NONE)
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, SimpleScheduler.this::save);
            }
        };
        runnable.runTaskTimer(plugin, 0, 20*45);

        return this;
    }

    @Override
    public Scheduler disable() {
        isEnabled = false;
        runnable.cancel();
        runnable = null;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public Scheduler executes(Consumer<ScheduledContext> executionHandler) {
        this.executionHandler = executionHandler;
        return this;
    }

    @Override
    public Consumer<ScheduledContext> getExecutionHandler() {
        return executionHandler;
    }

    @Override
    public void enableMissedSchedules(@NotNull File dataFile, @NotNull MissedScheduleStrategy strategy) {
        Objects.requireNonNull(dataFile, "dataFile cannot be null");
        Objects.requireNonNull(strategy, "strategy cannot be null");
        this.dataFile = dataFile;
        this.missedScheduleStrategy = strategy;
    }

    @Override
    public void disableMissedSchedules() {
        this.missedScheduleStrategy = MissedScheduleStrategy.NONE;
        this.dataFile = null;
    }

    @Override
    public File getDataFile() {
        return dataFile;
    }

    @Override
    public MissedScheduleStrategy getMissedScheduleStrategy() {
        return missedScheduleStrategy;
    }

    public void check() {
        LocalDateTime checking = normalize(getLastChecked()).plusMinutes(1);
        LocalDateTime now = normalize(LocalDateTime.now(schedule.getZoneId()));

        if (now.equals(checking)) return;

        // First handle missed schedules if relevant
        if (missedScheduleStrategy != MissedScheduleStrategy.NONE) {
            List<ScheduledContext> matches = new ArrayList<>();

            // Loop through all missed times
            while (checking.isBefore(now)) {
                matches.addAll(checkTasksForTime(checking));
                checking = checking.plusMinutes(1);
            }

            if (missedScheduleStrategy == MissedScheduleStrategy.CALL_ALL_MISSED)
                matches.forEach(this::trigger);
            else if (!matches.isEmpty()) {
                        matches.stream()
                        .max(Comparator.comparing(ctx -> LocalDateTime.of(ctx.getDate(), ctx.getTime())))
                                .ifPresent(this::trigger);
            }
        }

        // Handle now
        checkTasksForTime(now).forEach(this::trigger);

        setLastChecked(now);
    }

    public void setLastChecked(LocalDateTime lastChecked) {
        config.set("last-checked", lastChecked.toString());
    }

    public LocalDateTime getLastChecked() {
        String lastChecked = config.getString("last-checked");
        if (lastChecked == null) return LocalDateTime.now().minusMinutes(1);
        return LocalDateTime.parse(lastChecked);
    }

    public void save() {
        if (dataFile != null) {
            try {
                config.save(dataFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<ScheduledContext> checkTasksForTime(LocalDateTime checking) {
        List<ScheduledContext> matches = new ArrayList<>();

        LocalDate date = checking.toLocalDate();
        LocalTime time = checking.toLocalTime();
        DayOfWeek dayOfWeek = checking.getDayOfWeek();

        for (Schedule.DailyTask task : schedule.getDailyTasks())
            if (task.getTimes().contains(time))
                matches.add(new ScheduledContext(TaskType.DAILY, task.getData(), date, time, dayOfWeek));

        for (Schedule.WeeklyTask task : schedule.getWeeklyTasks())
            if (task.getDayOfWeek() == dayOfWeek && task.getTimes().contains(time))
                matches.add(new ScheduledContext(TaskType.WEEKLY, task.getData(), date, time, dayOfWeek));

        for (Schedule.MonthlyTask task : schedule.getMonthlyTasks())
            if (task.getDayOfMonth() == date.getDayOfMonth() && task.getTimes().contains(time))
                matches.add(new ScheduledContext(TaskType.MONTHLY, task.getData(), date, time, dayOfWeek));

        for (Schedule.DateTask task : schedule.getDateTasks())
            if (task.getDate().equals(date) && task.getTimes().contains(time))
                matches.add(new ScheduledContext(TaskType.DATE, task.getData(), date, time, dayOfWeek));

        return matches;
    }

    private LocalDateTime normalize(LocalDateTime time) {
        return time.withSecond(0).withNano(0);
    }
}
