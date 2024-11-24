package dev.splityosis.sysengine.commandlib.arguments;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.exception.InvalidInputException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StringCollectionArgument implements CommandArgument<String>{

    private String name;
    private Collection<String> collection;
    private boolean isCaseSensitive = false;

    public StringCollectionArgument(String name, Collection<String> collection) {
        this.name = name;
        this.collection = collection;
    }

    public StringCollectionArgument(String name, Collection<String> collection, boolean caseSensitive) {
        this.name = name;
        this.collection = collection;
        this.isCaseSensitive = caseSensitive;
    }

    @Override
    public String getName() {
        return name;
    }

    public Collection<String> getCollection() {
        return collection;
    }

    public StringCollectionArgument setCollection(Collection<String> collection) {
        this.collection = collection;
        return this;
    }

    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public StringCollectionArgument setCaseSensitive(boolean caseSensitive) {
        isCaseSensitive = caseSensitive;
        return this;
    }

    @Override
    public String parse(CommandSender sender, String input, Command command, int index, CommandContext context) throws InvalidInputException {

        if (isCaseSensitive()) {
            for (String s : getCollection())
                if (s.equals(input))
                    return s;
        }
        else
            for (String s : getCollection())
                if (s.equalsIgnoreCase(input)) return s;

        throw new InvalidInputException();
    }

    @Override
    public void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context, InvalidInputException inputException) {
        sender.sendMessage(ChatColor.RED + "Invalid input! invalid option at '"+input+"'.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String input, Command command, int index, RawCommandContext context) {
        return getCollection().stream()
                .filter(str -> str.toLowerCase().startsWith(input.toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }
}
