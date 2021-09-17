package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface SixArgsFunctionWithEx<T,N, A,B,C,D,E, EX extends Exception> extends AbstractMethod {
    N apply(T element, A arg1, B arg2, C arg3, D arg4, E arg5) throws EX;
}
