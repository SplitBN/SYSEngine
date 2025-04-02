package dev.splityosis.sysengine.common.collections;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class CircularArrayList<T> extends ArrayList<T> {

    private int i = 0;

    public CircularArrayList() {
    }

    public CircularArrayList(@NotNull Collection<? extends T> c) {
        super(c);
    }

    @Override
    public T get(int index) {
        if (isEmpty()) return null;
        return super.get(index % size());
    }

    public T getNext() {
        return get(i++);
    }

    public void reset() {
        i = 0;
    }
}
