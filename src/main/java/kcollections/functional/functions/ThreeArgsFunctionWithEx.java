package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface ThreeArgsFunctionWithEx<T,N, A,B, EX extends Exception> extends AbstractMethod {
    N apply(T element, A arg1, B arg2) throws EX;
}
