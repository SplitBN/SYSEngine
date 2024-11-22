package dev.splityosis.sysengine.utils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for handling time-related operations and formatting.
 *
 * <p>This class provides various static methods to format time in different ways, including:
 * <ul>
 *     <li>Formatting timestamps into human-readable strings.</li>
 *     <li>Calculating time differences.</li>
 *     <li>Parsing date difference strings into timestamps.</li>
 *     <li>Formatting dates according to specified patterns and time zones.</li>
 * </ul>
 *
 * <p>Common use cases include displaying time remaining, formatting dates for display, and
 * converting time durations into readable formats. The class is designed to be thread-safe
 * and can be used without the need for instantiation.
 *
 * <p>Example usage:
 * <pre>
 * long currentTimeMillis = System.currentTimeMillis();
 * String formattedDate = TimeUtil.formatDate(currentTimeMillis);
 * String timeRemaining = TimeUtil.getFormalTimeRemaining(currentTimeMillis + 60000); // 1 minute from now
 * </pre>
 *
 * <p>Note: All time values are expected to be in milliseconds. The class uses
 * {@link SimpleDateFormat} for date formatting, which is not thread-safe. However, since
 * each method creates a new instance of {@link SimpleDateFormat}, this is mitigated.
 *
 * <p>For more detailed usage of each method, refer to the individual method documentation.
 * </p>
 */
public class TimeUtil {

    private static final SimpleDateFormat format = new SimpleDateFormat("EEE MM/dd/yy");

    /**
     * Formats long time into a readable format this defaults to a short formatted time from
     * getFormalTimeRemaining i.e. 2m 3s
     *
     * @param millisecondsTimestamp the time to format
     * @return the formatted time
     */

    public static String getFormalTimeRemaining(long millisecondsTimestamp) {
        return getFormalTimeRemaining(millisecondsTimestamp, false);
    }

    /**
     * Formats time to date format i.e. EEE MM/dd/yy
     *
     * @param time the time to format
     * @return the formatted time
     */

    public static String formatDate(long time) {
        return format.format(new Date(time));
    }

    /**
     * Prettier way to format a time with {@link SimpleDateFormat}
     *
     * @param time      the time to format
     * @param formatter the simple date format
     * @return the formatted time
     */

    public static String formatDate(long time, String formatter) {
        return (new SimpleDateFormat(formatter)).format(new Date(time));
    }

    /**
     * Same as {@link #formatDate(long, String)} but with a actual {@link SimpleDateFormat} object.
     *
     * @param time   the time to format
     * @param format the simple date format
     * @return the formatted time
     */

    public static String formatDate(long time, SimpleDateFormat format) {
        return format.format(new Date(time));
    }

    /**
     * Formats timestamp to a readable format either extended format like 1 minute 2 seconds or short
     * format like 1m 2s
     *
     * @param millisecondsTimestamp the time to format
     * @param useShortUnits         whether to use short units or not
     * @return the formatted time
     */

    public static String getFormalTimeRemaining(long millisecondsTimestamp, boolean useShortUnits) {
        long span = millisecondsTimestamp - System.currentTimeMillis();
        String ret = getFormalTimeFromMillis(span, useShortUnits);
        return ret.isEmpty() ? "0 seconds" : (useShortUnits ? "0s" : ret);
    }

    /**
     * Formats past-time in the {@link #getFormalTimeFromMillis(long, boolean)} format does the minus
     * operation for you
     *
     * @param pastTime the time to format
     * @param useShort whether to use short units or not
     * @return the formatted time
     */

    public static String formatTimeSincePastTime(long pastTime, boolean useShort) {
        return getFormalTimeFromMillis(System.currentTimeMillis() - pastTime, useShort);
    }

    /**
     * Formats future-time in the {@link #getFormalTimeFromMillis(long, boolean)} format does the
     * minus operation for you but for future time
     *
     * @param timestamp the time to format
     * @return the formatted time
     */

    public static String formatFutureTime(long timestamp) {
        return getFormalTimeFromMillis(timestamp - System.currentTimeMillis());
    }

    /**
     * Formats future-time in the {@link #getFormalTimeFromMillis(long, boolean)} format does the
     * minus operation for you but for future time but allows you to include seconds
     *
     * @param timestamp      the time to format
     * @param useShortUnits  whether to use short units or not
     * @param includeSeconds whether to include seconds or not
     * @return the formatted time
     */

    public static String formatFutureTime(long timestamp, boolean useShortUnits,
                                          boolean includeSeconds) {
        return getFormalTimeFromMillis(timestamp - System.currentTimeMillis(), useShortUnits,
                includeSeconds);
    }

    /**
     * Format seconds, does the int to long conversion for you
     *
     * @param seconds the seconds to format
     * @return the formatted time
     */

    public static String formatSeconds(int seconds) {
        return getFormalTimeFromMillis(seconds * 1000L);
    }

    /**
     * Format seconds, does the int to long conversion for you but allows you to change the units
     * format.
     *
     * @param seconds    the seconds to format
     * @param shortUnits whether to use short units or not
     * @return the formatted time
     */

    public static String formatSeconds(int seconds, boolean shortUnits) {
        return getFormalTimeFromMillis(seconds * 1000L, shortUnits, true);
    }

    /**
     * Gets the formatted times from {@link #getFormalTimeFromMillis(long, boolean)} but defaults to
     * not use short units
     *
     * @param timeInMilliseconds the time to format
     * @return the formatted time
     */

    public static String getFormalTimeFromMillis(long timeInMilliseconds) {
        return getFormalTimeFromMillis(timeInMilliseconds, false);
    }

    /**
     * Gets the formatted times from {@link #getFormalTimeFromMillis(long, boolean, boolean)} but
     * defaults to true and INCLUDES SECONDS
     *
     * @param timeInMilliseconds the time to format
     * @param useShortUnits      whether to use short units or not
     * @return the formatted time
     */

    public static String getFormalTimeFromMillis(long timeInMilliseconds, boolean useShortUnits) {
        return getFormalTimeFromMillis(timeInMilliseconds, useShortUnits, true);
    }

    /**
     * Gets the seconds from a time in milliseconds
     *
     * @param timeInMilliseconds the time to format
     * @return the seconds
     */

    public static int getSecondsFromMillis(long timeInMilliseconds) {
        return (int) (timeInMilliseconds / 1000L);
    }

    /**
     * Formats time in milliseconds to a readable format
     *
     * @param timeInMilliseconds the time to format
     * @param useShortUnits      whether to use short units or not
     * @param includeSeconds     whether to include seconds or not
     * @return the formatted time
     */

    public static String getFormalTimeFromMillis(
            long timeInMilliseconds, boolean useShortUnits, boolean includeSeconds) {
        if (timeInMilliseconds < 60000L && !includeSeconds) {
            return useShortUnits ? "< 1m" : "< 1 minute";
        }
        if (timeInMilliseconds < 1000L) {
            return useShortUnits ? "0s" : "0 seconds";
        }
        StringBuilder time = new StringBuilder();
        double[] timesList = new double[4];
        String[] unitsList =
                useShortUnits ? new String[]{"s", "m", "h", "d"}
                        : new String[]{"second", "minute", "hour", "day"};
        timesList[0] = (timeInMilliseconds / 1000D);
        timesList[1] = Math.floor(timesList[0] / 60.0D);
        timesList[0] = timesList[0] - timesList[1] * 60.0D;
        timesList[2] = Math.floor(timesList[1] / 60.0D);
        timesList[1] = timesList[1] - timesList[2] * 60.0D;
        timesList[3] = Math.floor(timesList[2] / 24.0D);
        timesList[2] = timesList[2] - timesList[3] * 24.0D;
        for (int j = 3; j > -1; j--) {
            double d = timesList[j];
            if (d >= 1.0D) {
                if (j == 0 && !includeSeconds) {
                    break;
                }
                if (useShortUnits) {
                    time.append((int) d).append(unitsList[j]).append(" ");
                } else {
                    time.append((int) d).append(" ").append(unitsList[j]).append((d > 1.0D) ? "s " : " ");
                }
            }
        }
        return time.toString().trim();
    }

    /**
     * Parses a date diff string into a time stamp
     *
     * @param time   the time to parse
     * @param future whether the time is in the future or not
     * @return the time stamp
     * @throws Exception if the date is illegal
     */

    public static long parseDateDiff(String time, boolean future) throws Exception {
        Pattern timePattern = Pattern.compile(
                "(?:(\\d+)\\s*y[a-z]*[,\\s]*)?(?:(\\d+)\\s*mo[a-z]*[,\\s]*)?(?:(\\d+)\\s*w[a-z]*[,\\s]*)?(?:(\\d+)\\s*d[a-z]*[,\\s]*)?(?:(\\d+)\\s*h[a-z]*[,\\s]*)?(?:(\\d+)\\s*m[a-z]*[,\\s]*)?(?:(\\d+)\\s*(?:s[a-z]*)?)?",
                Pattern.CASE_INSENSITIVE);
        Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found) {
            throw new Exception("Illegal Date");
        }
        if (years > 20) {
            throw new Exception("Illegal Date");
        }
        Calendar c = new GregorianCalendar();
        if (years > 0) {
            c.add(Calendar.YEAR, years * (future ? 1 : -1));
        }
        if (months > 0) {
            c.add(Calendar.MONTH, months * (future ? 1 : -1));
        }
        if (weeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        }
        if (days > 0) {
            c.add(Calendar.DATE, days * (future ? 1 : -1));
        }
        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        }
        if (minutes > 0) {
            c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        }
        if (seconds > 0) {
            c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
        }
        return c.getTimeInMillis() / 1000L;
    }

    /**
     * Formats time to date format i.e. EEE MM/dd/yy
     *
     * @param time     the time to format
     * @param format   The simple date format
     * @param timeZone The time zone to use
     * @return the formatted time
     */

    public static String formatTimeToDate(long time, String format, String timeZone) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String dayNumberSuffix = getDayNumberSuffix(cal.get(Calendar.DATE));
        DateFormat dateFormat = new SimpleDateFormat(String.format(format, dayNumberSuffix));
        if (timeZone != null && !dateFormat.getTimeZone().getID().equals(timeZone)) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return dateFormat.format(cal.getTime());
    }

    /**
     * Gets the day number suffix for specific day of the month. i.e. 1st, 2nd, 3rd, 4th, etc.
     *
     * @param day The day of the month.
     * @return The day number suffix.
     */

    private static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        return switch (day % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
}