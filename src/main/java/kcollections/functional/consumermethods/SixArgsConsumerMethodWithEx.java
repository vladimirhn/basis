package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface SixArgsConsumerMethodWithEx<T,A,B,C,D,E, EX extends Exception> extends AbstractMethod {
    void apply(T data, A arg1, B arg2, C arg3, D arg4, E arg5) throws EX;
}
