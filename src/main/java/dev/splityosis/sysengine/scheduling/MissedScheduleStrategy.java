package dev.splityosis.sysengine.scheduling;

/**
 * Defines how missed scheduled tasks are handled.
 */
public enum MissedScheduleStrategy {

    /**
     * Call all missed tasks since the last check.
     */
    CALL_ALL,

    /**
     * Call only the latest missed task.
     */
    CALL_ONLY_LAST,

    /**
     * Do not call any missed tasks.
     */
    NONE;
}