package kcollections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class StandartCollectionWrapper <T> implements Collection<T> {

    protected abstract Collection<T> getJCollection();

    //Имплементация (через делегирование) стандартных методов коллекций
    @Override
    public int size() {
        return getJCollection().size();
    }

    @Override
    public boolean isEmpty() {
        return getJCollection().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getJCollection().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return getJCollection().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        getJCollection().forEach(action);
    }

    @Override
    public Object[] toArray() {
        return getJCollection().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return getJCollection().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return getJCollection().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return getJCollection().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getJCollection().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return getJCollection().addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getJCollection().removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return getJCollection().removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getJCollection().retainAll(c);
    }

    @Override
    public void clear() {
        getJCollection().clear();
    }

    @Override
    public Spliterator<T> spliterator() {
        return getJCollection().spliterator();
    }

    @Override
    public Stream<T> stream() {
        return getJCollection().stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return getJCollection().parallelStream();
    }



}
