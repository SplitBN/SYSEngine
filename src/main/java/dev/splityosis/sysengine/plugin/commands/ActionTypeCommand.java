package dev.splityosis.sysengine.plugin.commands;

import dev.splityosis.sysengine.actions.ActionType;
import dev.splityosis.sysengine.actions.ActionTypeRegistry;
import dev.splityosis.sysengine.commandlib.arguments.IntegerArgument;
import dev.splityosis.sysengine.commandlib.arguments.StringArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.plugin.commands.arguments.ActionTypeArgument;
import dev.splityosis.sysengine.utils.ColorUtil;
import dev.splityosis.sysengine.utils.VersionUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ActionTypeCommand extends Command {

    private static final int ITEMS_PER_PAGE = 5;

    public ActionTypeCommand() {
        super("actiontypes", "actiontype");
        description("Provides documentation for registered ActionTypes");

        addSubCommands(
                new Command("list")
                        .description("List all registered ActionTypes with their parameters")
                        .optionalArguments(new IntegerArgument("page"))
                        .executes((sender, context) -> {
                            int page = (int) context.getArgOrDefault("page", 1);
                            showActionTypesList(sender, page);
                        }),

                new Command("search")
                        .description("Search for a specific ActionType by name")
                        .arguments(new ActionTypeArgument("actiontype"))
                        .executes((sender, context) -> {
                            ActionType actionType = (ActionType) context.getArg("actiontype");
                            showActionTypeDetails(sender, actionType);
                        })
        );
    }

    private void showActionTypesList(CommandSender sender, int page) {
        List<ActionType> actionTypes = new ArrayList<>(ActionTypeRegistry.get().getAllActionTypes());
        int totalItems = actionTypes.size();
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

        if (page < 1 || page > totalPages) {
            sender.sendMessage(ColorUtil.colorize("&cInvalid page number. Total pages: " + totalPages));
            return;
        }

        sender.sendMessage(ColorUtil.colorize("&aRegistered ActionTypes (Page " + page + " of " + totalPages + "):"));
        sender.sendMessage(ColorUtil.colorize("&a(Hover for more info)"));

        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);

        for (ActionType actionType : actionTypes.subList(startIndex, endIndex)) {
            sendComponent(sender, buildActionTypeComponent(actionType));
        }
    }

    private void showActionTypeDetails(CommandSender sender, ActionType actionType) {
        sender.sendMessage(ColorUtil.colorize("&aDetails for ActionType: " + actionType.getName()));
        sender.sendMessage(ColorUtil.colorize("&a(Hover for more info)"));
        sender.spigot().sendMessage(buildActionTypeComponent(actionType));
    }

    private BaseComponent[] buildActionTypeComponent(ActionType actionType) {
        StringBuilder actionTypeDisplay = new StringBuilder();
        actionTypeDisplay.append("&b- ").append(actionType.getName()).append(" ");

        for (String param : actionType.getParameters()) {
            actionTypeDisplay.append("{").append(param).append("} ");
        }

        for (String optParam : actionType.getOptionalParameters()) {
            actionTypeDisplay.append("{*").append(optParam).append("} ");
        }

        TextComponent actionTypeText = new TextComponent(TextComponent.fromLegacyText(ColorUtil.colorize(actionTypeDisplay.toString())));

        StringBuilder hoverText = new StringBuilder();
        hoverText.append("&eDescription: &f").append(actionType.getDescription()).append("\n")
                .append("&eAliases: &f").append(String.join(", ", actionType.getAliases()));

        actionTypeText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ColorUtil.colorize(hoverText.toString()))));

        return new BaseComponent[]{actionTypeText};
    }

    private static BaseComponent[] parseComponentText(String rawText) {
        String[] lines = rawText.split("\n");
        TextComponent combined = new TextComponent();

        for (int i = 0; i < lines.length; i++) {
            TextComponent lineComponent = new TextComponent(TextComponent.fromLegacyText(ColorUtil.colorize("&r" + lines[i])));
            combined.addExtra(lineComponent);
            if (i < lines.length - 1)
                combined.addExtra("\n");
        }

        return new BaseComponent[]{combined};
    }

    private static BaseComponent[] parseComponentText(List<String> rawText) {
        TextComponent combined = new TextComponent();
        for (int i = 0; i < rawText.size(); i++) {
            BaseComponent[] lineComponents = parseComponentText(rawText.get(i));
            for (BaseComponent component : lineComponents)
                combined.addExtra(component);

            if (i < rawText.size() - 1)
                combined.addExtra("\n");
        }

        return new BaseComponent[]{combined};
    }

    private static void sendComponent(CommandSender sender, BaseComponent[] components) {
        if (VersionUtil.isServerAtLeast("1.9"))
            sender.spigot().sendMessage(components);
        else {
            if (sender instanceof Player)
                ((Player) sender).spigot().sendMessage(components);
            else {
                StringBuilder text = new StringBuilder();
                for (BaseComponent component : components)
                    text.append(component.toPlainText());
                sender.sendMessage(text.toString());
            }
        }
    }
}
