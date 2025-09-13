package dev.splityosis.sysengine.guilib.events;

import dev.splityosis.sysengine.guilib.components.GuiPage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class GuiPagePlayerInventoryClickEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private GuiPage guiPage;
    private Player player;
    private InventoryClickEvent inventoryClickEvent;
    public GuiPagePlayerInventoryClickEvent(GuiPage guiPage, Player player, InventoryClickEvent inventoryClickEvent) {
        this.guiPage = guiPage;
        this.player = player;
        this.inventoryClickEvent = inventoryClickEvent;
    }

    public GuiPage getGuiPage() {
        return guiPage;
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryClickEvent getInventoryClickEvent() {
        return inventoryClickEvent;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}

