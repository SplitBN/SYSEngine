package dev.splityosis.sysengine.test;

import dev.splityosis.sysengine.commandlib.arguments.BooleanArgument;
import dev.splityosis.sysengine.commandlib.arguments.PlayerArgument;
import dev.splityosis.sysengine.commandlib.arguments.StringArgument;
import dev.splityosis.sysengine.commandlib.arguments.StringCollectionArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TestCommand extends Command {

    public TestCommand(TestConfig config, ConfigManager configManager) {
        super("Config", "Settings");

        addSubCommands(
                new Command("Get", "Read")
                        .description("Gets the value from the config.")
                        .arguments(
                                new StringCollectionArgument("field", Arrays.asList("Split", "Volkoff", "Silly"))
                        )
                        .executes((sender, context) -> {
                            if (context.getRawArg("field").equalsIgnoreCase("split"))
                                sender.sendMessage(config.split);
                            else
                                sender.sendMessage(config.volkoff);
                        }),


                new Command("Set", "Write")
                        .description("Sets the value in the config.")
                        .arguments(
                                new StringCollectionArgument("field", Arrays.asList("Split", "Volkoff", "Silly")),
                                new StringArgument("value"),
                                new PlayerArgument("player")
                        )
                        .executes((sender, context) -> {
                            if (context.getRawArg("field").equalsIgnoreCase("split"))
                                config.split =  context.getRawArg("value");
                            else
                                config.volkoff = context.getRawArg("value");

                            try {
                                configManager.save(config);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            sender.sendMessage("done");
                        }),

                new Command("Test")
                        .description("Test command with optional arguments.")
                        .arguments(
                                new PlayerArgument("player")
                        )
                        .optionalArguments(
                                new BooleanArgument("doGiveDiamond")
                        )
                        .executes((sender, context) -> {

                            context.logRawArguments();
                            context.logArguments();

                            Player player = context.getArgAs("player", Player.class);
                            boolean giveDiamond = context.getArgAsOrDefault("doGiveDiamond", Boolean.class, false);

                            player.sendMessage("You have been tested on");
                            if (giveDiamond) {
                                player.getInventory().addItem(new ItemStack(Material.DIAMOND));
                                player.sendMessage("Ding");
                            }
                        })
        );
    }
}
