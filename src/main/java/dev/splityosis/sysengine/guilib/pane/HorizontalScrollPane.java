package dev.splityosis.sysengine.guilib.pane;

import dev.splityosis.sysengine.common.collections.OffsetList;
import dev.splityosis.sysengine.guilib.components.GuiItem;
import dev.splityosis.sysengine.guilib.components.GuiPage;
import dev.splityosis.sysengine.guilib.components.PaneLayout;
import dev.splityosis.sysengine.guilib.exceptions.UnsupportedPaneOperationException;
import dev.splityosis.sysengine.guilib.intenral.AbstractPane;

import java.util.*;

public class HorizontalScrollPane extends AbstractPane<HorizontalScrollPane> {

    private int logicalWidth;
    private int logicalHeight;

    private OffsetList<GuiItem[]> columns = new OffsetList<>();
    private int columnOffset = 0;

    private Map<Integer, GuiItem> items = new HashMap<>();
    boolean populated = false;

    public HorizontalScrollPane(int logicalWidth, int logicalHeight, PaneLayout layout) {
        super(layout);
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
    }

    @Override
    protected void onAttach(GuiPage page) {
        // Nothing
    }

    @Override
    protected void onDirectItemSet(int localSlot, GuiItem item) throws UnsupportedPaneOperationException {
        int col = getColumnIndexAt(localSlot);
        int row = getRowIndexAt(localSlot);
        ensureColumnExists(col);
        getColumn(col)[row] = item;
    }

    @Override
    public Map<Integer, GuiItem> getLocalItems() {
        if (!populated) {
            populated = true;
            updateVisibleItems();
        }
        return items;
    }

    public int getMinColumn() {
        return columns.getMinIndex();
    }

    public int getMaxColumn() {
        return columns.getMaxIndex();
    }

    public int getColumnCount() {
        return columns.size();
    }

    public GuiItem[] getColumn(int column) {
        return columns.get(column);
    }

    public HorizontalScrollPane clear() {
        columns.clear();
        return this;
    }

    public HorizontalScrollPane clearColumn(int column) {
        GuiItem[] colItems = columns.get(column);
        for (int i = 0; i < logicalHeight; i++)
            colItems[i] = null;

        return this;
    }

    public HorizontalScrollPane removeColumn(int column) {
        columns.removeAt(column);
        return this;
    }

    public HorizontalScrollPane setColumn(int column, GuiItem[] items) {
        ensureColumnExists(column);

        GuiItem[] colItems = columns.get(column);
        for (int i = 0; i < logicalHeight; i++)
            colItems[i] = i < items.length ? items[i] : null;

        return this;
    }

    public HorizontalScrollPane setItem(int column, int row, GuiItem item) {
        ensureColumnExists(column);
        if (row < 0 ||row >= logicalHeight)
            throw new IndexOutOfBoundsException("Row "+row+"out of bounds [0 - "+logicalHeight+"]");

        GuiItem[] columnItems = columns.get(column);
        columnItems[row] = item;
        return this;
    }

    public int getColumnIndexAt(int slot) {
        return columnOffset + slot % logicalWidth;
    }

    public int getRowIndexAt(int slot) {
        return slot / logicalWidth;
    }

    public HorizontalScrollPane setScrollOffset(int offset) {
        this.columnOffset = offset;
        updateVisibleItems();
        return this;
    }

    public HorizontalScrollPane scrollLeft(int columns) {
        return setScrollOffset(this.columnOffset - columns);
    }

    public HorizontalScrollPane scrollRight(int columns) {
        return setScrollOffset(this.columnOffset + columns);
    }

    public int getScrollOffset() {
        return this.columnOffset;
    }

    public HorizontalScrollPane scrollToIncludeColumn(int column) {
        if (column < columnOffset) {
            return setScrollOffset(column);
        }
        if (column >= columnOffset + logicalWidth) {
            return setScrollOffset(column - logicalWidth + 1);
        }
        return this;
    }

    public List<GuiItem[]> getVisibleColumns() {
        List<GuiItem[]> visible = new ArrayList<>(logicalWidth);
        for (int col = 0; col < logicalWidth; col++) {
            int logicalCol = columnOffset + col;
            if (columns.containsIndex(logicalCol)) {
                visible.add(columns.get(logicalCol));
            } else {
                visible.add(null);
            }
        }
        return visible;
    }

    public int getLogicalHeight() {
        return logicalHeight;
    }

    public int getLogicalWidth() {
        return logicalWidth;
    }

    private void updateVisibleItems() {
        items.clear();
        for (int col = 0; col < logicalWidth; col++) {
            int logicalCol = columnOffset + col;
            if (!columns.containsIndex(logicalCol)) continue;

            GuiItem[] colItems = columns.get(logicalCol);
            for (int row = 0; row < logicalHeight; row++) {
                GuiItem item = colItems[row];
                if (item != null) {
                    int slot = row * logicalWidth + col;
                    items.put(slot, item);
                }
            }
        }
    }

    private void ensureColumnExists(int column) {
        while (columns.getMinIndex() > column)
            columns.addFront(new GuiItem[logicalHeight]);

        while (columns.getMaxIndex() < column)
            columns.addBack(new GuiItem[logicalHeight]);
    }


}
