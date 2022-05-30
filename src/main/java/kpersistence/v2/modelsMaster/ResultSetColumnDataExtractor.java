package kpersistence.v2.modelsMaster;

@FunctionalInterface
public interface ResultSetColumnDataExtractor<T,N, EX extends Exception> {
    N apply(T element) throws EX;
}
