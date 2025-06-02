package dev.splityosis.sysengine.guilib.events;

import dev.splityosis.sysengine.guilib.components.GuiItem;
import dev.splityosis.sysengine.guilib.components.Pane;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class GuiItemPreClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private Pane pane;
    private Player player;
    private InventoryClickEvent inventoryClickEvent;
    private int localSlot;
    private int rawSlot;
    private GuiItem guiItem;

    public GuiItemPreClickEvent(Pane pane, Player player, InventoryClickEvent inventoryClickEvent, int localSlot, int rawSlot, GuiItem guiItem) {
        this.pane = pane;
        this.player = player;
        this.inventoryClickEvent = inventoryClickEvent;
        this.localSlot = localSlot;
        this.rawSlot = rawSlot;
        this.guiItem = guiItem;
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

    public GuiItem getGuiItem() {
        return guiItem;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
