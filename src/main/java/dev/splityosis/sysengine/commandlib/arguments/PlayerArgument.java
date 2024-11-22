package dev.splityosis.sysengine.commandlib.arguments;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.exception.InvalidInputException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerArgument implements CommandArgument<Player> {

    private final String name;

    public PlayerArgument(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Player parse(CommandSender sender, String input, Command command, int index, CommandContext context) throws InvalidInputException {
        Player player = Bukkit.getPlayer(input);
        if (player == null) {
            throw new InvalidInputException();
        }
        return player;
    }

    @Override
    public void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context) {
        sender.sendMessage(ChatColor.RED + "Player '" + input + "' is not online!");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String input, Command command, int index, RawCommandContext context) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }
}
