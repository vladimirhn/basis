package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface FourArgsConsumerMethodWithEx<T,A,B,C, EX extends Exception> extends AbstractMethod {
    void apply(T data, A arg1, B arg2, C arg3) throws EX;
}
