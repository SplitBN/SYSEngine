package dev.splityosis.sysengine.plugin.commands;

import dev.splityosis.sysengine.commandlib.command.Command;

public class ActionsCommand extends Command {

    public ActionsCommand() {
        super("actions", "action");
        permission("sysengine.command.actions");
        description("Actions command branch");

        addSubCommands(new ActionTypeCommand());

    }
}
