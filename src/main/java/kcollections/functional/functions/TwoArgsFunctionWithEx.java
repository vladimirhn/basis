package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface TwoArgsFunctionWithEx<T,N, A, EX extends Exception> extends AbstractMethod {
    N apply(T element, A arg1) throws EX;
}
