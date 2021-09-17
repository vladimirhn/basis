package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface ThreeArgsConsumerMethodWithEx<T,A,B, EX extends Exception> extends AbstractMethod {
    void apply(T data, A arg1, B arg2) throws EX;
}
