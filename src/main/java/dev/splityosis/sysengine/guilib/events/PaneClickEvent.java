package dev.splityosis.sysengine.guilib.events;

import dev.splityosis.sysengine.guilib.components.Pane;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class PaneClickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Pane pane;
    private Player player;
    private InventoryClickEvent inventoryClickEvent;
    private int localSlot;
    private int rawSlot;

    public PaneClickEvent(Pane pane, Player player, InventoryClickEvent inventoryClickEvent, int localSlot, int rawSlot) {
        this.pane = pane;
        this.player = player;
        this.inventoryClickEvent = inventoryClickEvent;
        this.localSlot = localSlot;
        this.rawSlot = rawSlot;
    }

    public Pane getPane() {
        return pane;
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryClickEvent getInventoryClickEvent() {
        return inventoryClickEvent;
    }

    public int getLocalSlot() {
        return localSlot;
    }

    public int getRawSlot() {
        return rawSlot;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
