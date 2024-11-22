package dev.splityosis.sysengine.commandlib.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandContext extends RawCommandContext {

    private final Map<String, Object> parsedArgs;
    private Object[] parsedArgsArray;

    public CommandContext(Command command, String label, List<Command> parentCommands, LinkedHashMap<String, String> rawArgs, LinkedHashMap<String, Object> parsedArgs) {
        super(command, label, parentCommands, rawArgs);
        this.parsedArgs = parsedArgs;
        this.parsedArgsArray = parsedArgs.values().toArray(Object[]::new);
    }

    /**
     * Gets the parsed argument by its name.
     */
    public Object getArg(String name) {
        return parsedArgs.get(fixArgName(name));
    }

    /**
     * Gets the parsed argument by name or returns a default value if not present.
     */
    public Object getArgOrDefault(String name, Object defaultValue) {
        return parsedArgs.getOrDefault(fixArgName(name), defaultValue);
    }

    /**
     * Gets the parsed argument by index.
     */
    public Object getArg(int index) {
        if (index >= 0 && index < parsedArgsArray.length)
            return parsedArgsArray[index];
        return null;
    }

    /**
     * Gets the parsed argument by index or returns a default value if not present.
     */
    public Object getArgOrDefault(int index, Object defaultValue) {
        Object arg = getArg(index);
        return (arg != null) ? arg : defaultValue;
    }

    /**
     * Gets the parsed argument by name, cast to the specified type.
     */
    public <T> T getArgAs(String name, Class<T> clazz) {
        Object value = getArg(name);
        if (value != null) {
            return clazz.cast(value);
        }
        return null;
    }

    /**
     * Gets the parsed argument by name, cast to the specified type, or returns a default value if not present.
     */
    public <T> T getArgAsOrDefault(String name, Class<T> clazz, T defaultValue) {
        Object value = getArg(name);
        if (value != null) {
            return clazz.cast(value);
        }
        return defaultValue;
    }

    /**
     * Gets the parsed argument by index, cast to the specified type.
     */
    public <T> T getArgAs(int index, Class<T> clazz) {
        Object value = getArg(index);
        if (value != null) {
            return clazz.cast(value);
        }
        return null;
    }

    /**
     * Gets the parsed argument by index, cast to the specified type, or returns a default value if not present.
     */
    public <T> T getArgAsOrDefault(int index, Class<T> clazz, T defaultValue) {
        Object value = getArg(index);
        if (value != null) {
            return clazz.cast(value);
        }
        return defaultValue;
    }

    /**
     * Logs all parsed arguments.
     */
    public void logArguments() {
        parsedArgs.forEach((key, value) -> System.out.println("Argument " + key + ": " + value));
    }

    /**
     * Gets an array of the parsed objects.
     * @return Array of the arguments.
     */
    public Object[] getArgs() {
        return parsedArgsArray;
    }

    /**
     * This updates necessary fields after one of the arg maps is changed.
     * This is mainly used internally.
     */
    @Override
    public void update() {
        super.update();
        this.parsedArgsArray = parsedArgs.values().toArray(Object[]::new);
    }
}
