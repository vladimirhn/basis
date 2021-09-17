package kcollections.functional.collmethods;

import kcollections.functional.AbstractMethod;

import java.util.Collection;

@FunctionalInterface
public interface FiveArgsCollectionMethod<T, A,B,C,D,E> extends AbstractMethod {
    Collection<T> apply(A arg1, B arg2, C arg3, D arg4, E arg5);
}
