package dev.splityosis.sysengine.guilib;

import dev.splityosis.sysengine.guilib.components.Gui;
import dev.splityosis.sysengine.guilib.components.GuiPage;
import dev.splityosis.sysengine.guilib.components.Pane;
import dev.splityosis.sysengine.guilib.events.GuiCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageCloseEvent;
import dev.splityosis.sysengine.guilib.events.PaneCloseEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiLibListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null) return;
        if (! (holder instanceof GuiPage)) return;
        event.setCancelled(true);
        GuiPage page = (GuiPage) holder;
        page.handleClick(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onGuiClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null) return;
        if (! (holder instanceof GuiPage)) return;

        GuiPage page = (GuiPage) holder;
        Gui gui = page.getParentGui();
        Player player = (Player) event.getPlayer();

        gui.getOnClose().call(new GuiCloseEvent(gui, player));
        page.getOnClose().call(new GuiPageCloseEvent(page, player));

        for (int i = page.getPanes().size() - 1; i >= 0; i--) {
            Pane pane = page.getPanes().get(i);
            if (pane.isVisible())
                pane.getOnClose().call(new PaneCloseEvent(pane, player));
        }
    }


}
