package dev.splityosis.sysengine.common.function;

@FunctionalInterface
public interface TriSupplier<T, U, V, R> {
    R get(T t, U u, V v);
}
