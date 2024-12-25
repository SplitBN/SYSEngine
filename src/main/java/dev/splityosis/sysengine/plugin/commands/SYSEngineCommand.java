package dev.splityosis.sysengine.plugin.commands;

import dev.splityosis.sysengine.commandlib.command.Command;

public class SYSEngineCommand extends Command {


    public SYSEngineCommand() {
        super("sysengine", "SEngine", "SYSEngine", "sengine");
        permission("sysengine.command");
        description("SYSEngine's main command");

        addSubCommands(new ActionsCommand());
    }
}
