package dev.splityosis.sysengine.guilib.events;

import dev.splityosis.sysengine.guilib.components.GuiPage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuiPageCloseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private GuiPage page;
    private Player player;


    public GuiPageCloseEvent(GuiPage page, Player player) {
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
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
