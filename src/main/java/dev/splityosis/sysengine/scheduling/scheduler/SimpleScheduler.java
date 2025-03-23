package dev.splityosis.sysengine.scheduling.scheduler;

import dev.splityosis.sysengine.scheduling.MissedScheduleStrategy;
import dev.splityosis.sysengine.scheduling.ScheduledContext;
import dev.splityosis.sysengine.scheduling.TaskType;
import dev.splityosis.sysengine.scheduling.schedule.Schedule;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SimpleScheduler implements Scheduler {

    private Schedule schedule;
    private MissedScheduleStrategy missedScheduleStrategy = MissedScheduleStrategy.NONE;
    private File dataFile;
    private Consumer<ScheduledContext> executionHandler;
    private boolean isEnabled = false;
    private final Plugin plugin;
    private final long checksPeriod;
    private BukkitRunnable runnable;

    private FileConfiguration config = new YamlConfiguration();

    public SimpleScheduler(Plugin plugin) {
        this(plugin, 20*45);
    }

    public SimpleScheduler(Plugin plugin, long checksPeriod) {
        this.plugin = plugin;
        this.checksPeriod = checksPeriod;
    }

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
        try {
            executionHandler.accept(context);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Scheduler caught an exception while triggering task (" + context.getTaskIdentifier() +  "): ", e);
        }
        return this;
    }

    @Override
    public Scheduler enable() {
        isEnabled = true;

        if (runnable != null)
            runnable.cancel();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    check();

                    if (missedScheduleStrategy != MissedScheduleStrategy.NONE)
                        save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        runnable.runTaskTimerAsynchronously(plugin, 0, checksPeriod);
        return this;
    }

    @Override
    public Scheduler disable() {
        isEnabled = false;
        if (runnable != null)
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
    public Scheduler enableMissedSchedules(@NotNull File dataFile, @NotNull MissedScheduleStrategy strategy) {
        Objects.requireNonNull(dataFile, "dataFile cannot be null");
        Objects.requireNonNull(strategy, "strategy cannot be null");
        this.dataFile = dataFile;
        if (!dataFile.exists()) {
            File parentDir = dataFile.getParentFile();
            if (parentDir != null && !parentDir.exists())
                parentDir.mkdirs();

            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(dataFile);
        this.missedScheduleStrategy = strategy;

        return this;
    }

    @Override
    public Scheduler disableMissedSchedules() {
        this.missedScheduleStrategy = MissedScheduleStrategy.NONE;
        this.dataFile = null;
        return this;
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
        if (schedule == null) {
            return;
        }
        LocalDateTime checking = normalize(getLastChecked());
        LocalDateTime now = normalize(LocalDateTime.now(schedule.getZoneId()));



        if (now.equals(checking)) return;

        checking = checking.plusMinutes(1);

        // First handle missed schedules if relevant
        if (missedScheduleStrategy != MissedScheduleStrategy.NONE) {
            List<ScheduledContext> matches = new ArrayList<>();
            // Loop through all missed times
            while (checking.isBefore(now)) {
                matches.addAll(checkTasksForTime(checking));
                checking = checking.plusMinutes(1);
            }

            if (missedScheduleStrategy == MissedScheduleStrategy.CALL_ALL)
                Bukkit.getScheduler().runTask(plugin, () -> {
                    matches.stream()
                            .sorted(Comparator.comparing(ctx -> LocalDateTime.of(ctx.getDate(), ctx.getTime())))
                            .forEach(this::trigger);
                });

            else if (!matches.isEmpty()) {
                matches.stream()
                        .max(Comparator.comparing(ctx -> LocalDateTime.of(ctx.getDate(), ctx.getTime())))
                        .ifPresent(scheduledContext -> Bukkit.getScheduler().callSyncMethod(plugin, () -> trigger(scheduledContext)));
            }
        }

        // Handle now
        List<ScheduledContext> nowTasks = checkTasksForTime(now);
        Bukkit.getScheduler().runTask(plugin, () -> nowTasks.forEach(this::trigger));

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

        for (Schedule.DailyTaskSignature task : schedule.getDailyTaskSignatures())
            if (task.getTimes().contains(time))
                matches.add(new ScheduledContext(TaskType.DAILY, task.getIdentifier(), date, time, dayOfWeek));

        for (Schedule.WeeklyTaskSignature task : schedule.getWeeklyTaskSignatures())
            if (task.getDayOfWeek() == dayOfWeek && task.getTimes().contains(time))
                matches.add(new ScheduledContext(TaskType.WEEKLY, task.getIdentifier(), date, time, dayOfWeek));

        for (Schedule.MonthlyTaskSignature task : schedule.getMonthlyTaskSignatures())
            if (task.getDayOfMonth() == date.getDayOfMonth() && task.getTimes().contains(time))
                matches.add(new ScheduledContext(TaskType.MONTHLY, task.getIdentifier(), date, time, dayOfWeek));

        for (Schedule.DateTaskSignature task : schedule.getDateTaskSignatures())
            if (task.getDate().equals(date) && task.getTimes().contains(time))
                matches.add(new ScheduledContext(TaskType.DATE, task.getIdentifier(), date, time, dayOfWeek));

        return matches;
    }

    private LocalDateTime normalize(LocalDateTime time) {
        return time.withSecond(0).withNano(0);
    }
}
