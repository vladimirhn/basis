package kcollections.functional.getters;

@FunctionalInterface
public interface IntegerSupplier<T> {
    Integer getInteger(T src);
}
