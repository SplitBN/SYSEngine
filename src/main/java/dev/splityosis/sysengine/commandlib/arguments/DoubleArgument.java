package dev.splityosis.sysengine.commandlib.arguments;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.exception.InvalidInputException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class DoubleArgument implements CommandArgument<Double> {

    private final String name;

    public DoubleArgument(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Double parse(CommandSender sender, String input, Command command, int index, CommandContext context) throws InvalidInputException {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    public void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context, InvalidInputException inputException) {
        sender.sendMessage(ChatColor.RED + "Invalid input! Please enter a valid decimal number.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String input, Command command, int index, RawCommandContext context) {
        return Collections.emptyList();
    }
}
