package kcollections.functional.getters;

@FunctionalInterface
public interface DoubleSupplier<T> {
    Double getDouble(T src);
}
