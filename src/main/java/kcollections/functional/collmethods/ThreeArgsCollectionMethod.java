package kcollections.functional.collmethods;

import kcollections.functional.AbstractMethod;

import java.util.Collection;

@FunctionalInterface
public interface ThreeArgsCollectionMethod<T,A,B,C> extends AbstractMethod {
    Collection<T> apply(A arg1, B arg2, C arg3);
}
