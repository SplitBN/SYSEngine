package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.scheduling.schedule.Schedule;
import org.bukkit.Bukkit;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleMapper implements AbstractMapper<Schedule> {

    private static final String nullKeyIdentifier = "" + UUID.randomUUID() + UUID.randomUUID() + UUID.randomUUID();

    @Override
    public Schedule getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {

        String timezone = section.getString(path + ".timezone");
        ZoneId zoneId;

        if (timezone == null || timezone.isEmpty() || timezone.equalsIgnoreCase("system"))
            zoneId = ZoneId.systemDefault();
        else
            zoneId = ZoneId.of(timezone);

        Schedule schedule = new Schedule(zoneId);

        // Handle when it is a list (that means all the "data" is null)
        if (section.isList(path + ".schedule")) {
            List<String> config = section.getStringList(path +  ".schedule");
            process(schedule, null, config);
        }

        // Handle when it is a section (that means "data" is provided)
        else if (section.isConfigurationSection(path + ".schedule")) {
            ConfigurationSection configSection = section.getConfigurationSection(path + ".schedule");

            configSection.getKeys(false).forEach(data -> {
                List<String> config = configSection.getStringList(data);
                process(schedule, data, config);
            });
        }

        else
            Bukkit.getLogger().warning("Invalid schedule at: " + path);

        return schedule;
    }

    @Override
    public void setInConfig(ConfigManager manager, Schedule schedule, ConfigurationSection section, String path) {
        ZoneId zoneId = schedule.getZoneId();
        if (zoneId == null)
            zoneId = ZoneId.systemDefault();

        section.set(path + ".timezone", zoneId.getId());
        Map<String, List<String>> linesByData = buildLines(schedule);

        boolean hasNullData = linesByData.containsKey(nullKeyIdentifier);

        if (hasNullData) {
            List<String> allLines = new ArrayList<>();
            for (List<String> lines : linesByData.values()) {
                allLines.addAll(lines);
            }
            section.set(path + ".schedule", allLines);

        } else {
            for (Map.Entry<String, List<String>> entry : linesByData.entrySet()) {
                String dataKey = entry.getKey();
                List<String> lines = entry.getValue();
                section.set(path + ".schedule." + dataKey, lines);
            }
        }
    }

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static void process(Schedule schedule, String data, List<String> config) {

        for (String line : config) {
            String cleaned = line.replace(" at ", " ")
                    .replace(" on ", " ")
                    .trim();
            String[] parts = cleaned.split("\\s+", 3);

            if (parts.length < 2) continue;

            String type = parts[0].toUpperCase();

            try {
                switch (type) {
                    case "DAILY": {
                        String timesStr = cleaned.substring(6).trim();
                        List<LocalTime> times = parseTimes(timesStr);
                        schedule.addDaily(data, times.toArray(new LocalTime[0]));
                        break;
                    }
                    case "WEEKLY": {
                        if (parts.length < 3) continue;
                        DayOfWeek day = DayOfWeek.valueOf(parts[1].toUpperCase());
                        List<LocalTime> times = parseTimes(parts[2]);
                        schedule.addWeekly(data, day, times.toArray(new LocalTime[0]));
                        break;
                    }
                    case "MONTHLY": {
                        if (parts.length < 3) continue;
                        int dayOfMonth = Integer.parseInt(parts[1]);
                        List<LocalTime> times = parseTimes(parts[2]);
                        schedule.addMonthly(data, dayOfMonth, times.toArray(new LocalTime[0]));
                        break;
                    }
                    case "DATE": {
                        if (parts.length < 3) continue;
                        LocalDate date = LocalDate.parse(parts[1], dateFormatter);
                        List<LocalTime> times = parseTimes(parts[2]);
                        schedule.addDate(data, date, times.toArray(new LocalTime[0]));
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Invalid schedule type: " + type);
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("invalid schedule config : " + line);
            }
        }
    }

    private static List<LocalTime> parseTimes(String timesStr) {
        String[] rawTimes = timesStr.split(",");
        List<LocalTime> times = new ArrayList<>();

        for (String raw : rawTimes) {
            String timeStr = raw.trim().toUpperCase(Locale.ROOT);

            try {
                LocalTime time;

                if (timeStr.endsWith("AM") || timeStr.endsWith("PM")) {
                    DateTimeFormatter amPmFormatter;

                    if (timeStr.matches("\\d{1,2}[AP]M")) {
                        amPmFormatter = DateTimeFormatter.ofPattern("hha");
                    } else if (timeStr.matches("\\d{1,2}:\\d{2}[AP]M")) {
                        amPmFormatter = DateTimeFormatter.ofPattern("h:mma");
                    } else if (timeStr.matches("\\d{1,2} [AP]M")) {
                        timeStr = timeStr.replace(" ", "");
                        amPmFormatter = DateTimeFormatter.ofPattern("hha");
                    } else {
                        throw new DateTimeException("Invalid AM/PM format: " + timeStr);
                    }

                    time = LocalTime.parse(timeStr, amPmFormatter);
                } else {
                    if (timeStr.matches("^\\d{1,2}$")) {
                        timeStr += ":00";
                    }

                    time = LocalTime.parse(timeStr);
                }

                times.add(time.withSecond(0).withNano(0));

            } catch (DateTimeException e) {
                Bukkit.getLogger().warning("invalid time format: '" + timeStr + "' error: " + e.getMessage());
            }
        }

        return times;
    }

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static String formatTimes(List<LocalTime> times) {
        return times.stream()
                .map(localTime ->  localTime.format(timeFormatter))
                .collect(Collectors.joining(", "));
    }


    private Map<String, List<String>> buildLines(Schedule schedule) {
        Map<String, List<String>> linesByData = new LinkedHashMap<>();

        for (Schedule.DailyTaskSignature task : schedule.getDailyTaskSignatures()) {
            String dataKey = (task.getIdentifier() == null ? nullKeyIdentifier : task.getIdentifier());
            String line = "DAILY at " + formatTimes(task.getTimes());
            linesByData.computeIfAbsent(dataKey, k -> new ArrayList<>()).add(line);
        }

        for (Schedule.WeeklyTaskSignature task : schedule.getWeeklyTaskSignatures()) {
            String dataKey = (task.getIdentifier() == null ? nullKeyIdentifier : task.getIdentifier());
            String day = task.getDayOfWeek().toString();
            String line = "WEEKLY on " + day + " at " + formatTimes(task.getTimes());
            linesByData.computeIfAbsent(dataKey, k -> new ArrayList<>()).add(line);
        }

        for (Schedule.MonthlyTaskSignature task : schedule.getMonthlyTaskSignatures()) {
            String dataKey = (task.getIdentifier() == null ? nullKeyIdentifier : task.getIdentifier());
            String line = "MONTHLY on " + task.getDayOfMonth() + " at " + formatTimes(task.getTimes());
            linesByData.computeIfAbsent(dataKey, k -> new ArrayList<>()).add(line);
        }

        for (Schedule.DateTaskSignature task : schedule.getDateTaskSignatures()) {
            String dataKey = (task.getIdentifier() == null ? nullKeyIdentifier : task.getIdentifier());
            LocalDate date = task.getDate();
            String dateStr = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String line = "DATE on " + dateStr + " at " + formatTimes(task.getTimes());
            linesByData.computeIfAbsent(dataKey, k -> new ArrayList<>()).add(line);
        }

        return linesByData;
    }
}
