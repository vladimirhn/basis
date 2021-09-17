package kcollections.functional.collmethods;

import kcollections.functional.AbstractMethod;

import java.util.Collection;

@FunctionalInterface
public interface NoArgsCollectionMethod<T> extends AbstractMethod {
    Collection<T> apply();
}
