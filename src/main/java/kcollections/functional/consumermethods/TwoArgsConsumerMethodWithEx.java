package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface TwoArgsConsumerMethodWithEx<T,A, EX extends Exception> extends AbstractMethod {
    void apply(T data, A arg1) throws EX;
}
