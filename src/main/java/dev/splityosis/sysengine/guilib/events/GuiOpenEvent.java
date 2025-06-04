package dev.splityosis.sysengine.guilib.events;

import dev.splityosis.sysengine.guilib.components.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class GuiOpenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Gui gui;
    private Player player;
    private InventoryOpenEvent inventoryOpenEvent;

    public GuiOpenEvent(Gui gui, Player player, InventoryOpenEvent inventoryOpenEvent) {
        this.gui = gui;
        this.player = player;
        this.inventoryOpenEvent = inventoryOpenEvent;
    }

    public Gui getGui() {
        return gui;
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryOpenEvent getInventoryOpenEvent() {
        return inventoryOpenEvent;
    }

    @Override
    public boolean isCancelled() {
        return inventoryOpenEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean b) {
        inventoryOpenEvent.setCancelled(b);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }


}
