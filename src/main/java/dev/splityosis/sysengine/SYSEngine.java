package dev.splityosis.sysengine;

import com.cryptomorin.xseries.XMaterial;
import dev.splityosis.sysengine.commandlib.CommandLib;
import dev.splityosis.sysengine.commandlib.arguments.EnumArgument;
import dev.splityosis.sysengine.commandlib.arguments.IntegerArgument;
import dev.splityosis.sysengine.commandlib.arguments.PlayerArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class SYSEngine extends JavaPlugin {

    @Override
    public void onEnable() {

        CommandLib.createCommandManager(this).registerCommands(
                new Command("Give", "Item", "GiveItem")
                        .description("Gives item to player")
                        .permission("example.give")
                        .arguments(
                                new PlayerArgument("Receiver"),
                                new EnumArgument<>("Item", Material.class)
                        )
                        .optionalArguments(
                                new IntegerArgument("Amount")
                        )
                        .executes((sender, context) -> {

                            Player receiver = (Player) context.getArg("Receiver");
                            Material material = (Material) context.getArg("Item");
                            int amount = (Integer) context.getArgOrDefault("Amount", 1);

                            ItemStack item = XMaterial.matchXMaterial(material).parseItem(); // Using XSeries for multi-version compatibility
                            item.setAmount(amount);

                            receiver.getInventory().addItem(item);
                        })
        );

    }



    @Override
    public void onDisable() {

    }

}
