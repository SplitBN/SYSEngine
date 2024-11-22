package dev.splityosis.sysengine.commandlib.command;

import dev.splityosis.sysengine.commandlib.arguments.CommandArgument;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class RawCommandContext {

    private Command command;
    private final String label;
    private final List<Command> parentCommands;
    private final LinkedHashMap<String, String> rawArgs;
    private String[] rawArgsArray;

    public RawCommandContext(Command command, String label, List<Command> parentCommands, LinkedHashMap<String, String> rawArgs) {
        this.command = command;
        this.label = label;
        this.parentCommands = parentCommands;
        this.rawArgs = rawArgs;
        this.rawArgsArray = rawArgs.values().toArray(String[]::new);
    }

    /**
     * Gets the entire raw form of the command entered by the sender.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the raw argument by its name.
     */
    public String getRawArg(String name) {
        return rawArgs.get(fixArgName(name));
    }

    /**
     * Gets the raw argument by name or returns a default value if not present.
     */
    public String getRawArgOrDefault(String name, String defaultValue) {
        return rawArgs.getOrDefault(fixArgName(name), defaultValue);
    }

    /**
     * Gets the raw argument by index.
     */
    public String getRawArg(int index) {
        if (index >= 0 && index < rawArgsArray.length)
            return rawArgsArray[index];
        return null;
    }

    /**
     * Gets the raw argument by index or returns a default value if not present.
     */
    public String getRawArgOrDefault(int index, String defaultValue) {
        String arg = getRawArg(index);
        return arg != null ? arg : defaultValue;
    }

    /**
     * Gets the chain of commands used to get to this command.
     */
    public List<Command> getParentCommands() {
        return parentCommands;
    }

    /**
     * Logs all raw arguments.
     */
    public void logRawArguments() {
        rawArgs.forEach((key, value) -> System.out.println("Raw Argument " + key + ": " + value));
    }

    /**
     * Returns the total count of arguments.
     */
    public int getArgsCount() {
        return rawArgs.size();
    }

    /**
     * Checks if a raw argument with the specified name exists.
     */
    public boolean hasArgument(String name) {
        return rawArgs.containsKey(fixArgName(name));
    }

    /**
     * Checks if a raw argument with the specified index exists.
     */
    public boolean hasArgument(int index) {
        return index >= 0 && index < rawArgsArray.length;
    }

    /**
     * Gets an array of the raw arguments.
     * @return Array of the raw arguments.
     */
    public String[] getRawArgs() {
        return rawArgsArray;
    }

    /**
     * This updates necessary fields after one of the arg maps is changed.
     * This is mainly used internally.
     */
    public void update() {
        this.rawArgsArray = rawArgs.values().toArray(String[]::new);
    }

    protected String fixArgName(String name) {
        // Its valid and is in args
        if (rawArgs.containsKey(name))
            return name;

        // Check optional args first cuz its more likely that the unfilled arg is an optional
        for (CommandArgument<?> optionalArgument : command.getOptionalArguments())
            if (name.equalsIgnoreCase(optionalArgument.getName()))
                return optionalArgument.getName();

        // Finally check required args
        for (CommandArgument<?> argument : command.getArguments())
            if (name.equalsIgnoreCase(argument.getName()))
                return argument.getName();

        throw new RuntimeException("No such argument: " + name);
    }
}
