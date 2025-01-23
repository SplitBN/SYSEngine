package dev.splityosis.sysengine.plugin.commands.arguments;

import dev.splityosis.sysengine.actions.ActionType;
import dev.splityosis.sysengine.actions.ActionTypeRegistry;
import dev.splityosis.sysengine.commandlib.arguments.CommandArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.exception.InvalidInputException;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class ActionTypeArgument implements CommandArgument<ActionType> {

    private String name;

    public ActionTypeArgument(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ActionType parse(CommandSender sender, String input, Command command, int index, CommandContext context) throws InvalidInputException {
        ActionType actionType = ActionTypeRegistry.get().getActionType(input);
        if (actionType == null)
            throw new InvalidInputException("Invalid action type");
        return actionType;
    }

    @Override
    public void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context, InvalidInputException inputException) {
        sender.sendMessage(ColorUtil.colorize("&cInvalid action type '"+input+"'"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String input, Command command, int index, RawCommandContext context) {
        String finalInput = input.toLowerCase();
        return ActionTypeRegistry.get().getAllActionTypes()
                .stream()
                .map(ActionType::getName)
                .filter(string -> string.toLowerCase().startsWith(finalInput))
                .sorted()
                .collect(Collectors.toList());
    }
}
