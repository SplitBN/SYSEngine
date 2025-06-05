package dev.splityosis.sysengine.guilib.components;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * A data class that holds "The GuiItems that the GuiPage recognizes from the Pane".
 */
public class PaneLayer {

    private final Pane pane;
    private final Map<Integer, GuiItem> localToItemMap = new HashMap<>();
    private final Map<Integer, GuiItem> rawToItemMap = new HashMap<>();

    public PaneLayer(Pane pane) {
        this.pane = pane;
    }

    public GuiItem getItemAtRawSlot(int rawSlot) {
        return rawToItemMap.get(rawSlot);
    }

    public GuiItem getItemAtLocalSlot(int localSlot) {
        return localToItemMap.get(localSlot);
    }

    public void updateAll() {
        rawToItemMap.clear();
        localToItemMap.clear();

        localToItemMap.putAll(pane.getLocalItems());

        // Translate local to raw
        for (Map.Entry<Integer, GuiItem> e : localToItemMap.entrySet()) {
            int local = e.getKey();
            GuiItem item = e.getValue();
            pane.getLayout().toRawSlot(local).ifPresent(raw -> rawToItemMap.put(raw, item));
        }
    }

    public void updateRawSlot(int rawSlot) {
        pane.getLayout().toLocalSlot(rawSlot).ifPresent(local -> {
            GuiItem item = pane.getLocalItems().get(local);
            if (item == null) {
                rawToItemMap.remove(rawSlot);
                localToItemMap.remove(local);
            }
            else {
                rawToItemMap.put(rawSlot, item);
                localToItemMap.put(local, item);
            }
        });
    }

    public void updateLocalSlot(int localSlot) {
        pane.getLayout().toRawSlot(localSlot).ifPresent(rawSlot -> {
            GuiItem item = pane.getLocalItems().get(localSlot);
            if (item == null) {
                rawToItemMap.remove(rawSlot);
                localToItemMap.remove(localSlot);
            }
            else {
                rawToItemMap.put(rawSlot, item);
                localToItemMap.put(localSlot, item);
            }
        });
    }

    public Map<Integer, GuiItem> getRawToItemMap() {
        return rawToItemMap;
    }

    public Map<Integer, GuiItem> getLocalToItemMap() {
        return localToItemMap;
    }

    public Pane getPane() {
        return pane;
    }
}
