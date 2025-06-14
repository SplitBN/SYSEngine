package dev.splityosis.sysengine.guilib.events;

import dev.splityosis.sysengine.guilib.components.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class GuiItemClickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private InventoryClickEvent inventoryClickEvent;
    private int localSlot;
    private int rawSlot;
    private GuiItem guiItem;

    public GuiItemClickEvent(Player player, InventoryClickEvent inventoryClickEvent, int localSlot, int rawSlot, GuiItem guiItem) {
        this.player = player;
        this.inventoryClickEvent = inventoryClickEvent;
        this.localSlot = localSlot;
        this.rawSlot = rawSlot;
        this.guiItem = guiItem;
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
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
