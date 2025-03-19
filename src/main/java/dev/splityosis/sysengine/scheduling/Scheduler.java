package dev.splityosis.sysengine.scheduling;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Consumer;

public interface Scheduler {

    static Scheduler create() {
        return new SimpleScheduler();
    }

    Scheduler trigger(ScheduledContext context);

    Scheduler enable(Plugin plugin);

    Scheduler disable();

    boolean isEnabled();

    Scheduler setSchedule(@Nullable Schedule schedule);

    Schedule getSchedule();

    Scheduler executes(Consumer<ScheduledContext> executionHandler);

    Consumer<ScheduledContext> getExecutionHandler();

    void enableMissedSchedules(@NotNull File dataFile, @NotNull MissedScheduleStrategy strategy);

    void disableMissedSchedules();

    File getDataFile();

    MissedScheduleStrategy getMissedScheduleStrategy();

}
