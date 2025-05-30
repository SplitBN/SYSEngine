package dev.splityosis.sysengine.common.function;

@FunctionalInterface
public interface BiSupplier<T, U, R> {
    R get(T t, U u);
}