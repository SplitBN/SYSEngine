package dev.splityosis.sysengine.guilib.events;

import dev.splityosis.sysengine.guilib.components.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuiCloseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Gui gui;
    private Player player;

    public GuiCloseEvent(Gui gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    public Gui getGui() {
        return gui;
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
