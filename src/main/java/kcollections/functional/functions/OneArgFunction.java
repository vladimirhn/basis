package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface OneArgFunction<T,N> extends AbstractMethod {
    N apply(T element);
}
