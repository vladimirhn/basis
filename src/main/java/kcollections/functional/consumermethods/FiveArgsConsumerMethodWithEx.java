package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface FiveArgsConsumerMethodWithEx<T,A,B,C,D, EX extends Exception> extends AbstractMethod {
    void apply(T data, A arg1, B arg2, C arg3, D arg4) throws EX;
}
