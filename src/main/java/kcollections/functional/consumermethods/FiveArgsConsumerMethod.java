package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface FiveArgsConsumerMethod <T,A,B,C,D> extends AbstractMethod {
    void apply(T data, A arg1, B arg2, C arg3, D arg4);
}
