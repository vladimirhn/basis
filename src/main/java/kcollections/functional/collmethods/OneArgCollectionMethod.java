package kcollections.functional.collmethods;

import kcollections.functional.AbstractMethod;

import java.util.Collection;

@FunctionalInterface
public interface OneArgCollectionMethod<T, A> extends AbstractMethod {
    Collection<T> apply(A arg1);
}
