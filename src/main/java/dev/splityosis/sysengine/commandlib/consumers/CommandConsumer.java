package dev.splityosis.sysengine.commandlib.consumers;

import dev.splityosis.sysengine.commandlib.command.CommandContext;
import org.bukkit.command.CommandSender;

public interface CommandConsumer {

    void execute(CommandSender sender, CommandContext context);

}
