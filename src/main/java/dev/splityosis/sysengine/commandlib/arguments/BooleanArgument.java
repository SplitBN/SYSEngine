package dev.splityosis.sysengine.commandlib.arguments;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.exception.InvalidInputException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BooleanArgument implements CommandArgument<Boolean> {

    private final String name;

    public BooleanArgument(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean parse(CommandSender sender, String input, Command command, int index, CommandContext context) throws InvalidInputException {
        String normalizedInput = input.toLowerCase();

        if (normalizedInput.equals("true") || normalizedInput.equals("yes")) {
            return true;
        } else if (normalizedInput.equals("false") || normalizedInput.equals("no")) {
            return false;
        }

        throw new InvalidInputException();
    }

    @Override
    public void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context) {
        sender.sendMessage(ChatColor.RED + "Invalid input! Please enter 'true', 'false', 'yes', or 'no'.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String input, Command command, int index, RawCommandContext context) {
        return Arrays.asList("true", "false", "yes", "no").stream()
                .filter(value -> value.startsWith(input.toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

}
