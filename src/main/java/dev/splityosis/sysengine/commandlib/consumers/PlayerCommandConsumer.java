package dev.splityosis.sysengine.commandlib.consumers;

import dev.splityosis.sysengine.commandlib.command.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface PlayerCommandConsumer extends CommandConsumer {

    void executePlayer(Player sender, CommandContext context);

    @Override
    default void execute(CommandSender sender, CommandContext context) {
        executePlayer((Player) sender, context);
    }
}
