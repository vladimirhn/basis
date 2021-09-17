package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface TwoArgsConsumerMethod <T,A> extends AbstractMethod {
    void apply(T data, A arg1);
}
