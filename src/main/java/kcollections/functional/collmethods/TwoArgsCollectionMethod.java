package kcollections.functional.collmethods;

import kcollections.functional.AbstractMethod;

import java.util.Collection;

@FunctionalInterface
public interface TwoArgsCollectionMethod<T,A,B> extends AbstractMethod {
    Collection<T> apply(A arg1, B arg2);
}
