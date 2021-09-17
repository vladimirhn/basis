package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface FiveArgsFunctionWithEx<T,N, A,B,C,D, EX extends Exception> extends AbstractMethod {
    N apply(T element, A arg1, B arg2, C arg3, D arg4) throws EX;
}
