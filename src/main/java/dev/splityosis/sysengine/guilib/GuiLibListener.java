package dev.splityosis.sysengine.guilib;

import dev.splityosis.sysengine.guilib.components.Gui;
import dev.splityosis.sysengine.guilib.components.GuiPage;
import dev.splityosis.sysengine.guilib.components.Pane;
import dev.splityosis.sysengine.guilib.events.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiLibListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null) return;
        if (! (holder instanceof GuiPage)) return;
        GuiPage page = (GuiPage) holder;

//        System.out.println("Got the page, calling handleClick()");

        // Let page handle rest
        page.handleClick(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null) return;
        if (! (holder instanceof GuiPage)) return;
        GuiPage page = (GuiPage) holder;

        // Let page handle rest
        page.handleDrag(event);
    }


    @EventHandler
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
            pane.getOnClose().call(new PaneCloseEvent(pane, player));
        }
    }

    @EventHandler
    public void onGuiOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null) return;
        if (! (holder instanceof GuiPage)) return;

        GuiPage page = (GuiPage) holder;
        Gui gui = page.getParentGui();
        Player player = (Player) event.getPlayer();

        gui.getOnOpen().call(new GuiOpenEvent(gui, player, event));
        page.getOnOpen().call(new GuiPageOpenEvent(page, player, event));

        for (int i = page.getPanes().size() - 1; i >= 0; i--) {
            Pane pane = page.getPanes().get(i);
            pane.getOnOpen().call(new PaneOpenEvent(pane, player, event));
        }
    }

}
