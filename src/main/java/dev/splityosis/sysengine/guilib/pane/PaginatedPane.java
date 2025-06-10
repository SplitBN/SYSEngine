package dev.splityosis.sysengine.guilib.pane;

import dev.splityosis.sysengine.guilib.components.GuiItem;
import dev.splityosis.sysengine.guilib.components.GuiPage;
import dev.splityosis.sysengine.guilib.components.PaneLayout;
import dev.splityosis.sysengine.guilib.exceptions.UnsupportedPaneOperationException;
import dev.splityosis.sysengine.guilib.intenral.AbstractPane;

import java.util.*;

/**
 * A pane implementation that supports pagination, allowing items to be divided across multiple logical pages.
 * Each page maintains its own item map. Items can be added, removed, and displayed per page.
 */
public class PaginatedPane extends AbstractPane<PaginatedPane> {

    private final List<Map<Integer, GuiItem>> pages = new ArrayList<>();
    private int currentPageIndex = 0;

    /**
     * Constructs a new paginated pane using the specified layout.
     *
     * @param layout The layout to use for slot translation.
     */
    public PaginatedPane(PaneLayout layout) {
        super(layout);
        pages.add(new HashMap<>());
    }

    /**
     * Called when this pane is attached to a GUI page.
     *
     * @param page The page this pane is now part of.
     */
    @Override
    protected void onAttach(GuiPage page) {
    }

    /**
     * Called internally to directly set an item in a local slot, bypassing layout logic.
     *
     * @param localSlot The local slot to set the item in.
     * @param item      The item to set, or {@code null} to clear.
     * @throws UnsupportedPaneOperationException If the operation is not supported.
     */
    @Override
    protected void onDirectItemSet(int localSlot, GuiItem item) throws UnsupportedPaneOperationException {
        setItem(localSlot, item);
    }

    /**
     * Gets the item map of the currently active page.
     *
     * @return The map of local slots to items on the current page.
     */
    @Override
    public Map<Integer, GuiItem> getLocalItems() {
        return currentPageIndex >= pages.size() ? Collections.emptyMap() : pages.get(currentPageIndex);
    }

    /**
     * Sets an item in a specific page and slot.
     *
     * @param page The page index.
     * @param slot The slot index within that page.
     * @param item The item to set, or {@code null} to remove.
     * @return This pane.
     */
    public PaginatedPane setItem(int page, int slot, GuiItem item) {
        insurePageExists(page);
        Map<Integer, GuiItem> items = getPageItems(page);
        GuiItem previousItem = items.get(slot);

        if (previousItem != null)
            unregisterItem(previousItem);

        if (item != null) {
            items.put(slot, item);
            registerItem(item);
        } else {
            items.remove(slot);
        }

        return this;
    }

    /**
     * Sets an item in the current page and given slot.
     *
     * @param slot The local slot.
     * @param item The item to set or {@code null} to remove.
     * @return This pane.
     */
    public PaginatedPane setItem(int slot, GuiItem item) {
        return setItem(getCurrentPageIndex(), slot, item);
    }

    /**
     * Sets the current visible page.
     *
     * @param page The index of the page to display.
     * @return This pane.
     * @throws IndexOutOfBoundsException If the page index is invalid.
     */
    public PaginatedPane setPage(int page) {
        if (page < 0 || page >= getPageCount())
            throw new IndexOutOfBoundsException("Page index out of bounds '" + page + "' for size '" + getPageCount() + "'");
        currentPageIndex = page;
        return this;
    }

    /**
     * Adds a new empty page to the end of the list.
     *
     * @return This pane.
     */
    public PaginatedPane addPage() {
        pages.add(new HashMap<>());
        return this;
    }

    /**
     * Gets the total number of pages.
     *
     * @return Page count.
     */
    public int getPageCount() {
        return pages.size();
    }

    /**
     * Retrieves the item map for a given page.
     *
     * @param page The page index.
     * @return The map of local slot to item.
     * @throws IndexOutOfBoundsException If the page index is invalid.
     */
    public Map<Integer, GuiItem> getPageItems(int page) {
        if (page < 0 || page >= pages.size())
            throw new IndexOutOfBoundsException("Page index out of bounds '" + page + "' for size '" + getPageCount() + "'");
        return pages.get(page);
    }

    /**
     * Removes the first instance of a given item across all pages.
     *
     * @param item The item to remove.
     * @return This pane.
     */
    public PaginatedPane removeItem(GuiItem item) {
        for (Map<Integer, GuiItem> page : pages) {
            for (Map.Entry<Integer, GuiItem> e : page.entrySet()) {
                if (e.getValue().equals(item)) {
                    unregisterItem(item);
                    page.remove(e.getKey());
                    return this;
                }
            }
        }
        return this;
    }

    /**
     * Removes the item at the specified page and slot.
     *
     * @param page The page index.
     * @param slot The slot index.
     * @return This pane.
     */
    public PaginatedPane removeItem(int page, int slot) {
        Map<Integer, GuiItem> items = getPageItems(page);
        GuiItem item = items.remove(slot);
        if (item != null)
            unregisterItem(item);
        return this;
    }

    /**
     * Clears all items from a specific page.
     *
     * @param page The page index.
     * @return This pane.
     */
    public PaginatedPane clearPage(int page) {
        Map<Integer, GuiItem> items = getPageItems(page);
        items.values().forEach(this::unregisterItem);
        items.clear();
        return this;
    }

    /**
     * Removes a page and all its items.
     *
     * @param page The page index.
     * @return This pane.
     * @throws IndexOutOfBoundsException If the page index is invalid.
     */
    public PaginatedPane removePage(int page) {
        if (page < 0 || page >= pages.size())
            throw new IndexOutOfBoundsException("Page index out of bounds '" + page + "' for size '" + getPageCount() + "'");
        Map<Integer, GuiItem> removed = pages.remove(page);
        removed.values().forEach(this::unregisterItem);
        return this;
    }

    /**
     * Removes all empty pages at the end of the page list.
     *
     * @return This pane.
     */
    public PaginatedPane trimEnd() {
        for (int i = pages.size() - 1; i >= 0; i--) {
            Map<Integer, GuiItem> items = pages.get(i);
            if (items.isEmpty()) {
                pages.remove(i);
            } else {
                break;
            }
        }
        return this;
    }

    /**
     * Removes all empty pages at the beginning of the page list.
     *
     * @return This pane.
     */
    public PaginatedPane trimBeginning() {
        for (int i = 0; i < pages.size(); i++) {
            Map<Integer, GuiItem> items = pages.get(i);
            if (items.isEmpty()) {
                pages.remove(i);
                i--;
            } else {
                break;
            }
        }
        return this;
    }

    /**
     * Gets the page index and slot of a given item.
     *
     * @param guiItem The item to search for.
     * @return An array {pageIndex, slot} or {@code null} if not found.
     */
    public int[] getPageAndSlot(GuiItem guiItem) {
        for (int page = 0; page < pages.size(); page++) {
            for (Map.Entry<Integer, GuiItem> e : pages.get(page).entrySet()) {
                if (e.getValue().equals(guiItem))
                    return new int[]{page, e.getKey()};
            }
        }
        return null;
    }

    /**
     * Adds an item in the first available slot across all pages.
     * This method expands the page list if needed.
     * <p><b>Note:</b> This method should only be used after the pane is attached to a GUI,
     * as it depends on {@code getLayout().getCapacity()}.</p>
     */
    public int[] addItem(GuiItem item) {
        int capacity = getLayout().getCapacity();

        int pageIndex = 0;
        while (true) {
            insurePageExists(pageIndex);
            Map<Integer, GuiItem> items = pages.get(pageIndex);

            for (int slot = 0; slot < capacity; slot++) {
                if (!items.containsKey(slot)) {
                    setItem(pageIndex, slot, item);
                    pageIndex++;
                    return new int[]{pageIndex, slot};
                }
                pageIndex++;
            }
        }
    }

    /**
     * Gets the index of the current visible page.
     *
     * @return The current page index.
     */
    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    /**
     * Ensures the page at the given index exists by creating pages if needed.
     *
     * @param page The required page index.
     */
    private void insurePageExists(int page) {
        while (pages.size() <= page)
            pages.add(new HashMap<>());
    }
}
