package dev.splityosis.sysengine.common.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * A two-sided list that supports pushing/popping at both ends
 * while preserving “logical” indexes.
 *
 * @param <T> the element type
 */
public class OffsetList<T> {
    private final List<T> list = new ArrayList<>();
    private int offset = 0;

    public void clear() {
        list.clear();
        offset = 0;
    }

    /** Inserts value at the front; decrements min index. */
    public void addFront(T value) {
        list.add(0, value);
        offset--;
    }

    /** Appends value at the back; max index increases by 1. */
    public void addBack(T value) {
        list.add(value);
    }

    /** Removes and returns the front element; increments min index. */
    public T removeFront() {
        if (list.isEmpty()) {
            throw new IllegalStateException("List is empty");
        }
        T removed = list.remove(0);
        offset++;
        resetIfEmpty();
        return removed;
    }

    /** Removes and returns the back element; max index decreases by 1. */
    public T removeBack() {
        if (list.isEmpty()) {
            throw new IllegalStateException("List is empty");
        }
        T removed = list.remove(list.size() - 1);
        resetIfEmpty();
        return removed;
    }

    /**
     * Removes the first occurrence of value.
     * If it was at the very front, adjusts offset like removeFront().
     * @return true if removed, false otherwise
     */
    public boolean remove(T value) {
        int idx = list.indexOf(value);
        if (idx == -1) return false;
        list.remove(idx);
        if (idx == 0) {
            offset++;
            resetIfEmpty();
        }
        return true;
    }

    /**
     * Removes and returns the element at the given logical index.
     * @throws IndexOutOfBoundsException if index out of current bounds
     */
    public T removeAt(int logicalIndex) {
        int ii = logicalIndex - offset;
        if (ii < 0 || ii >= list.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + logicalIndex);
        }
        T removed = list.remove(ii);
        if (ii == 0) {
            offset++;
        }
        resetIfEmpty();
        return removed;
    }

    /**
     * Returns the element at the given logical index.
     * @throws IndexOutOfBoundsException if index out of current bounds
     */
    public T get(int logicalIndex) {
        int ii = logicalIndex - offset;
        if (ii < 0 || ii >= list.size())
            throw new IndexOutOfBoundsException("Invalid index: " + logicalIndex);
        return list.get(ii);
    }

    public void set(int logicalIndex, T value) {
        int ii = logicalIndex - offset;
        if (ii < 0 || ii >= list.size())
            throw new IndexOutOfBoundsException("Invalid index: " + logicalIndex);
        list.set(logicalIndex, value);
    }

    /** Returns true if logicalIndex ∈ [min, max]. */
    public boolean containsIndex(int logicalIndex) {
        int ii = logicalIndex - offset;
        return ii >= 0 && ii < list.size();
    }

    /** The smallest valid logical index currently in the list. */
    public int getMinIndex() {
        return offset;
    }

    /** The largest valid logical index currently in the list. */
    public int getMaxIndex() {
        return offset + list.size() - 1;
    }

    /** Number of elements in the list. */
    public int size() {
        return list.size();
    }

    /** True if the list has no elements. */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /** If list is empty, reset offset back to zero for a “fresh” state. */
    private void resetIfEmpty() {
        if (list.isEmpty()) {
            offset = 0;
        }
    }
}