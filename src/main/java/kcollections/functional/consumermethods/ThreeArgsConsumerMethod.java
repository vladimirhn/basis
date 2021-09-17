package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface ThreeArgsConsumerMethod <T,A,B> extends AbstractMethod {
    void apply(T data, A arg1, B arg2);
}
