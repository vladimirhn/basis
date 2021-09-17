package kcollections.functional.catchmethods;

import kcollections.functional.AbstractMethod;

import java.util.Collection;

public interface CollectionCatchMethod<T extends Collection> extends AbstractMethod {
    void apply(T data, Exception e);
}
