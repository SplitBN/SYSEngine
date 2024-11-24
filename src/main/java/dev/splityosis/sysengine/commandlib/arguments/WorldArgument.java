package dev.splityosis.sysengine.commandlib.arguments;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.exception.InvalidInputException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class WorldArgument implements CommandArgument<World> {

    private final String name;

    public WorldArgument(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public World parse(CommandSender sender, String input, Command command, int index, CommandContext context) throws InvalidInputException {
        World world = Bukkit.getWorld(input);

        if (world == null)
            throw new InvalidInputException();

        return world;
    }

    @Override
    public void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context, InvalidInputException inputException) {
        sender.sendMessage(ChatColor.RED + "Invalid world name! Please enter a valid world name.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String input, Command command, int index, RawCommandContext context) {
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(worldName -> worldName.toLowerCase().startsWith(input.toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }
}
