package kcollections.functional.functions;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface ThreeArgsFunction <T,N, A,B> extends AbstractMethod {
    N apply(T element, A arg1, B arg2);
}
