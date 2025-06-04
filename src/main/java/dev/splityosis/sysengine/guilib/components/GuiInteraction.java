package dev.splityosis.sysengine.guilib.components;

import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a type of interaction with a gui inventory.
 */
public enum GuiInteraction {

    ITEM_TAKE(false) {
        private final Set<InventoryAction> ACTIONS = EnumSet.of(
                InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME,
                InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL,
                InventoryAction.COLLECT_TO_CURSOR, InventoryAction.HOTBAR_SWAP,
                InventoryAction.MOVE_TO_OTHER_INVENTORY
        );

        @Override
        public boolean matches(InventoryInteractEvent event) {
            if (!(event instanceof InventoryClickEvent)) return false;
            InventoryClickEvent clickEvent = (InventoryClickEvent) event;

            Inventory inventory = clickEvent.getInventory();
            Inventory clicked = clickEvent.getClickedInventory();
            InventoryAction action = clickEvent.getAction();

            if ((clicked != null && clicked.getType() == InventoryType.PLAYER) || inventory.getType() == InventoryType.PLAYER)
                return false;

            return ACTIONS.contains(action);
        }
    },

    ITEM_PLACE(false) {
        private final Set<InventoryAction> ACTIONS = EnumSet.of(
                InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME,
                InventoryAction.PLACE_ALL
        );

        @Override
        public boolean matches(final InventoryInteractEvent event) {
            if (!(event instanceof InventoryClickEvent)) return false;

            InventoryClickEvent clickEvent = (InventoryClickEvent) event;

            Inventory inventory = clickEvent.getInventory();
            Inventory clicked = clickEvent.getClickedInventory();
            InventoryAction action = clickEvent.getAction();

            return (
                    action == InventoryAction.MOVE_TO_OTHER_INVENTORY && clicked != null &&
                            clicked.getType() == InventoryType.PLAYER && inventory.getType() != clicked.getType()
            ) || (
                    ACTIONS.contains(action) && (clicked == null || clicked.getType() != InventoryType.PLAYER) &&
                            inventory.getType() != InventoryType.PLAYER
            );
        }
    },

    ITEM_DROP(false) {
        private final Set<InventoryAction> ACTIONS = EnumSet.of(
                InventoryAction.DROP_ONE_SLOT, InventoryAction.DROP_ALL_SLOT,
                InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ALL_CURSOR
        );

        @Override
        public boolean matches(final InventoryInteractEvent event) {
            if (!(event instanceof InventoryClickEvent)) return false;
            InventoryClickEvent clickEvent = (InventoryClickEvent) event;


            Inventory inventory = clickEvent.getInventory();
            Inventory clicked = clickEvent.getClickedInventory();
            InventoryAction action = clickEvent.getAction();

            return ACTIONS.contains(action) && (clicked != null || inventory.getType() != InventoryType.PLAYER);
        }
    },

    ITEM_SWAP(false) {
        private final Set<InventoryAction> ACTIONS = EnumSet.of(
                InventoryAction.HOTBAR_SWAP, InventoryAction.SWAP_WITH_CURSOR,
                InventoryAction.HOTBAR_MOVE_AND_READD
        );

        @Override
        public boolean matches(final InventoryInteractEvent event) {
            if (!(event instanceof InventoryClickEvent)) return false;

            InventoryClickEvent clickEvent = (InventoryClickEvent) event;

            Inventory inventory = clickEvent.getInventory();
            Inventory clicked = clickEvent.getClickedInventory();
            InventoryAction action = clickEvent.getAction();

            return ACTIONS.contains(action) && (clicked == null || clicked.getType() != InventoryType.PLAYER) &&
                    inventory.getType() != InventoryType.PLAYER;
        }
    },

    ITEM_DRAG(false) {
        @Override
        public boolean matches(final InventoryInteractEvent event) {
            if (!(event instanceof InventoryDragEvent)) return false;
            InventoryDragEvent dragEvent = (InventoryDragEvent) event;

            int size = dragEvent.getView().getTopInventory().getSize();
            return dragEvent.getRawSlots().stream().anyMatch(slot -> slot < size);
        }
    },

    OTHER(false) {
        private final Set<InventoryAction> ACTIONS = EnumSet.of(
                InventoryAction.CLONE_STACK, InventoryAction.UNKNOWN, InventoryAction.NOTHING
        );

        @Override
        public boolean matches(final InventoryInteractEvent event) {
            if (!(event instanceof InventoryClickEvent)) return false;
            InventoryClickEvent clickEvent = (InventoryClickEvent) event;

            Inventory inventory = clickEvent.getInventory();
            Inventory clicked = clickEvent.getClickedInventory();
            InventoryAction action = clickEvent.getAction();

            return ACTIONS.contains(action) && (clicked != null || inventory.getType() != InventoryType.PLAYER);
        }
    };

    private final boolean isAllowedByDefault;

    GuiInteraction(final boolean isAllowedByDefault) {
        this.isAllowedByDefault = isAllowedByDefault;
    }

    public boolean isAllowedByDefault() {
        return isAllowedByDefault;
    }

    public boolean isDisallowedByDefault() {
        return !isAllowedByDefault;
    }

    public abstract boolean matches(InventoryInteractEvent event);

    public static final Set<GuiInteraction> ALL = Collections.unmodifiableSet(EnumSet.allOf(GuiInteraction.class));

    public static final Set<GuiInteraction> ALLOWED_BY_DEFAULT = Collections.unmodifiableSet(EnumSet.allOf(GuiInteraction.class)
            .stream()
            .filter(GuiInteraction::isAllowedByDefault)
            .collect(Collectors.toSet())
    );

    public static final Set<GuiInteraction> DISALLOWED_BY_DEFAULT = Collections.unmodifiableSet(EnumSet.allOf(GuiInteraction.class)
            .stream()
            .filter(GuiInteraction::isDisallowedByDefault)
            .collect(Collectors.toSet())
    );

}
