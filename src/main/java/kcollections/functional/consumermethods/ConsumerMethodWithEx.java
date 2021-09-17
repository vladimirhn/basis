package kcollections.functional.consumermethods;

import kcollections.functional.AbstractMethod;

@FunctionalInterface
public interface ConsumerMethodWithEx<T, EX extends Exception> extends AbstractMethod {
    void apply(T data) throws EX;
}
