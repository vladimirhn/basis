package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface ConsumerMethod<T> extends AbstractMethod {
    void apply(T data);
}
