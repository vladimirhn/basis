package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface TwoArgsFunction<T,N, A> extends AbstractMethod {
    N apply(T element, A arg1);
}
