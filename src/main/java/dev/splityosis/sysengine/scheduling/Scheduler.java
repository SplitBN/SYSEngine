package dev.splityosis.sysengine.scheduling;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Consumer;

/**
 * Schedules and triggers tasks based on a Schedule.
 * Supports missed task handling and custom execution logic.
 */
public interface Scheduler {

    /**
     * Creates a scheduler with default check interval.
     * @param plugin The plugin instance.
     * @return A new Scheduler.
     */
    static Scheduler create(Plugin plugin) {
        return new SimpleScheduler(plugin);
    }

    /**
     * Creates a scheduler with a custom check interval.
     * @param plugin The plugin instance.
     * @param checksPeriod Interval between checks (ticks).
     * @return A new Scheduler.
     */
    static Scheduler create(Plugin plugin, long checksPeriod) {
        return new SimpleScheduler(plugin, checksPeriod);
    }

    /**
     * Triggers a task manually.
     * @param context The context of the task.
     * @return This scheduler.
     */
    Scheduler trigger(ScheduledContext context);

    /**
     * Enables the scheduler.
     * @return This scheduler.
     */
    Scheduler enable();

    /**
     * Disables the scheduler.
     * @return This scheduler.
     */
    Scheduler disable();

    /**
     * Checks if the scheduler is enabled.
     * @return True if enabled.
     */
    boolean isEnabled();

    /**
     * Sets the schedule to use.
     * @param schedule The schedule.
     * @return This scheduler.
     */
    Scheduler setSchedule(@Nullable Schedule schedule);

    /**
     * Gets the current schedule.
     * @return The schedule, or null.
     */
    Schedule getSchedule();

    /**
     * Sets the execution handler for triggered tasks.
     * @param executionHandler Consumer to handle task execution.
     * @return This scheduler.
     */
    Scheduler executes(Consumer<ScheduledContext> executionHandler);

    /**
     * Gets the current execution handler.
     * @return The handler.
     */
    Consumer<ScheduledContext> getExecutionHandler();

    /**
     * Enables missed schedule handling.
     * @param dataFile File to store last-checked time.
     * @param strategy How missed tasks are handled.
     */
    void enableMissedSchedules(@NotNull File dataFile, @NotNull MissedScheduleStrategy strategy);

    /**
     * Disables missed schedule handling.
     */
    void disableMissedSchedules();

    /**
     * Gets the data file used for tracking.
     * @return The data file, or null.
     */
    File getDataFile();

    /**
     * Gets the missed schedule strategy.
     * @return The strategy.
     */
    MissedScheduleStrategy getMissedScheduleStrategy();
}

