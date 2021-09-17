package kcollections;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public abstract class StandartListWrapper <T> extends KCollection<KList<T>, List<T>, T> implements List<T> {

    protected abstract List<T> getJList();
    protected abstract KList<T> getCurrentKList();
    protected abstract KList<T> getKListDublicate();
    protected abstract KList<T> getKListDublicate(int motherCollectionSize);
    protected abstract KList<T> getNewKList();
    protected abstract <N> KList<N> getNewKList(Collection data);
    protected abstract KList streamToNewKCollectionWithMotherSize(Stream<T> stream);

    @Override
    public List<T> getJCollection() {
        return getJList();
    }

    @Override
    protected KList<T> getCurrentKCollection() {
        return getCurrentKList();
    }

    @Override
    public KList<T> getKCollectionDublicate() {
        return getKListDublicate();
    }

    @Override
    protected KList<T> getKCollectionDublicate(int motherCollectionSize) {
        return getKListDublicate(motherCollectionSize);
    }

    @Override
    public KList<T> getNewKCollection() {
        return getNewKList();
    }

    @Override
    protected KList<T> getNewKCollection(Collection data) {
        return getNewKList(data);
    }

    //Имплементация (через делегирование) стандартных методов списка
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return getJList().addAll(index, c);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        getJList().replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        getJList().sort(c);
    }

    @Override
    public T get(int index) {
        return getJList().get(index);
    }

    @Override
    public T set(int index, T element) {
        return getJList().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        getJList().add(index, element);
    }

    @Override
    public T remove(int index) {
        return getJList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getJList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getJList().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return getJList().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return getJList().listIterator(index);
    }

    @Override
    public KList<T> subList(int fromIndex, int toIndex) {
        return new KList<>(getJList().subList(fromIndex, toIndex));
    }
}
