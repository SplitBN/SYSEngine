package dev.splityosis.sysengine.guilib.events;

import dev.splityosis.sysengine.guilib.components.Pane;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class PaneOpenEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Pane pane;
    private Player player;
    private InventoryOpenEvent inventoryOpenEvent;

    public PaneOpenEvent(Pane pane, Player player, InventoryOpenEvent inventoryOpenEvent) {
        this.pane = pane;
        this.player = player;
        this.inventoryOpenEvent = inventoryOpenEvent;
    }

    public Pane getPane() {
        return pane;
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryOpenEvent getInventoryOpenEvent() {
        return inventoryOpenEvent;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
