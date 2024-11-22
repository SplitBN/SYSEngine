package dev.splityosis.sysengine.commandlib.arguments;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.exception.InvalidInputException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumArgument<T extends Enum<T>> implements CommandArgument<T> {

    private final String name;
    private final Class<T> enumType;

    public EnumArgument(String name, Class<T> enumType) {
        this.name = name;
        this.enumType = enumType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T parse(CommandSender sender, String input, Command command, int index, CommandContext context) throws InvalidInputException {
        try {
            return Enum.valueOf(enumType, input);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    public void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context) {
        String validValues = Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        sender.sendMessage(ChatColor.RED + "Invalid input! Please enter one of the following: " + validValues + ".");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String input, Command command, int index, RawCommandContext context) {
        return Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .filter(value -> value.toLowerCase().startsWith(input.toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }
}
