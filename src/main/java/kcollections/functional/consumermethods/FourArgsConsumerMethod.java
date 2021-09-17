package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface FourArgsConsumerMethod <T,A,B,C> extends AbstractMethod {
    void apply(T data, A arg1, B arg2, C arg3);
}
