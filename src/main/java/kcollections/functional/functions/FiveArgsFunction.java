package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface FiveArgsFunction <T,N, A,B,C,D> extends AbstractMethod {
    N apply(T element, A arg1, B arg2, C arg3, D arg4);
}
