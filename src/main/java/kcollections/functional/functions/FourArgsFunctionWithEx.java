package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface FourArgsFunctionWithEx<T,N, A,B,C, EX extends Exception> extends AbstractMethod {
    N apply(T element, A arg1, B arg2, C arg3) throws EX;
}
