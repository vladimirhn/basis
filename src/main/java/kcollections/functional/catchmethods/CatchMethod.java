package kcollections.functional.catchmethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface CatchMethod<T> extends AbstractMethod {
    void apply(T data, Exception e);
}
