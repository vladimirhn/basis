package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface OneArgFunctionWithEx<T,N, EX extends Exception> extends AbstractMethod {
    N apply(T element) throws EX;
}
