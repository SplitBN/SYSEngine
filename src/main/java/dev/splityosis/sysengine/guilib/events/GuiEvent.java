package dev.splityosis.sysengine.guilib.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

@FunctionalInterface
public interface GuiEvent<T extends Event> {

    void functional(T event);

    default void call(T event) {
        Bukkit.getPluginManager().callEvent(event);
        functional(event);
    };

}
