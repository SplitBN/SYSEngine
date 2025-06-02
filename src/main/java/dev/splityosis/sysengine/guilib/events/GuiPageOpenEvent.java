package dev.splityosis.sysengine.guilib.events;

import dev.splityosis.sysengine.guilib.components.GuiPage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuiPageOpenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private GuiPage page;
    private Player player;

    public GuiPageOpenEvent(GuiPage page, Player player) {
        this.page = page;
        this.player = player;
    }

    public GuiPage getPage() {
        return page;
    }

    public Player getPlayer() {
        return player;
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
