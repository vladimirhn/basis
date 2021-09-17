package kcollections;

import kcollections.functional.catchmethods.CollectionCatchMethod;
import kcollections.functional.consumermethods.*;
import kcollections.functional.functions.*;
import koptional.KOptional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KList<T> extends StandartListWrapper<T> {

    private final List<T> jList;
    CollectionCatchMethod<KList<T>> catchMethod = null;

    //Тип списка и его содержимое задаются на фабрике
    KList(List<T> jList) {
        this.jList = jList;
    }

    public KList() {
        this(new ArrayList<>());
    }

    @Override
    public List<T> getJList() {
        return jList;
    }

    @Override
    protected KList<T> getCurrentKList() {
        return this;
    }

    @Override
    protected KList<T> getKListDublicate() {

        List<T> dublicate = makeSameTypeList();

        dublicate.addAll(jList);

        return new KList<>(dublicate);
    }

    @Override
    protected KList<T> getKListDublicate(int motherCollectionSize) {

        List<T> dublicate = getKListDublicate();
        dublicate.addAll(jList);

        KList<T> kList = new KList<>(dublicate);
        kList.motherCollectionSize = motherCollectionSize;
        return kList;
    }

    @Override
    protected KList<T> getNewKList() {
        return new KList<>(makeSameTypeList());
    }

    @Override
    protected <N> KList<N> getNewKList(Collection data) {
        List<N> list = makeSameTypeList();
        list.addAll(data);
        return new KList<>(list);
    }

    @Override
    protected KList<T> streamToNewKCollectionWithMotherSize(Stream<T> stream) {

        KList<T> kList = new KList<>(stream.collect(Collectors.toList()));
        kList.motherCollectionSize = jList.size();
        return kList;
    }

    private <N> List<N> makeSameTypeList() {
        List<N> sameTypeList;
        try {
            sameTypeList = jList.getClass().newInstance();

        } catch (IllegalAccessException | InstantiationException e) {
            sameTypeList = new ArrayList<>();
        }

        return sameTypeList;
    }

    @Override
    public String toString() {
        return jList.toString();
    }

    //Сортировка

    private void updateJList(List<T> newList) {
        jList.clear();
        jList.addAll(newList);
    }

    public <U extends Comparable<? super U>> KList<T> sortAsc(Function<? super T, ? extends U> getter) {

        KList<T> nulls = getCurrentKList().filterNulls(getter);
        KList<T> nonNulls = getCurrentKList().minus(nulls);

        nonNulls.sort(Comparator.comparing(getter));
        nonNulls.addAll(nulls);

        updateJList(nonNulls);

        return nonNulls;
    }

    public <U extends Comparable<? super U>> KList<T> sortDesc(Function<? super T, ? extends U> getter) {
        getJList().sort(Comparator.comparing(getter));
        Collections.reverse(getJList());
        return this;
    }

    //Обработка каждого элемента списка

    //Отображение в коллекцию данных нового типа

    public <N, A, EX extends Exception> KList<N> mapEachBy(OneArgFunctionWithEx<A, N, EX> meth, Function<T,A> getter) throws EX {

        List<N> result = new ArrayList<>(getJList().size());
        for (T element : getJList()) {
            A fieldValue = getter.apply(element);
            result.add(meth.apply(fieldValue));
        }
        return getNewKList(result);
    }

    public <N, EX extends Exception> KList<N> mapEachBy(OneArgFunctionWithEx<T, N, EX> meth) throws EX {

        List<N> result = new ArrayList<>(getJList().size());
        for (T element : getJList()) {
            result.add(meth.apply(element));
        }
        return getNewKList(result);
    }

    public <N, A, EX extends Exception> KList<N> mapEachBy(TwoArgsFunctionWithEx<? super T,N, A, EX> meth, A arg1) throws EX {

        List<N> result = new ArrayList<>(getJList().size());
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1));
        }
        return getNewKList(result);
    }

    public <N, A,B, EX extends Exception> KList<N> mapEachBy(ThreeArgsFunctionWithEx<? super T,N, A,B, EX> meth, A arg1, B arg2) throws EX {

        List<N> result = new ArrayList<>(getJList().size());
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1, arg2));
        }
        return getNewKList(result);
    }

    public <N, A,B,C, EX extends Exception> KList<N> mapEachBy(FourArgsFunctionWithEx<? super T,N, A,B,C, EX> meth, A arg1, B arg2, C arg3) throws EX {

        List<N> result = new ArrayList<>(getJList().size());
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1, arg2, arg3));
        }
        return getNewKList(result);
    }

    public <N, A,B,C,D, EX extends Exception> KList<N> mapEachBy(FiveArgsFunctionWithEx<? super T,N, A,B,C,D, EX> meth, A arg1, B arg2, C arg3, D arg4) throws EX {

        List<N> result = new ArrayList<>(getJList().size());
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1, arg2, arg3, arg4));
        }
        return getNewKList(result);
    }

    public <N, A,B,C,D,E, EX extends Exception> KList<N> mapEachBy(SixArgsFunctionWithEx<? super T,N, A,B,C,D,E, EX> meth, A arg1, B arg2, C arg3, D arg4, E arg5) throws EX {

        List<N> result = new ArrayList<>(getJList().size());
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1, arg2, arg3, arg4, arg5));
        }
        return getNewKList(result);
    }

    //Отображение в коллекцию данных нового типа в try/catch
    public <N, EX extends Exception> KList<N> tryMapEachBy(OneArgFunctionWithEx<T, N, EX> meth) {

        List<N> result = new ArrayList<>(getJList().size());

        getJList().forEach(element -> {
            try {
                result.add(meth.apply(element));
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });
        return getNewKList(result);
    }

    public <N, A, EX extends Exception> KList<N> tryMapEachBy(TwoArgsFunctionWithEx<? super T,N, A, EX> meth, A arg1) {

        List<N> result = new ArrayList<>(getJList().size());

        getJList().forEach(element -> {
            try {
                result.add(meth.apply(element, arg1));
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });
        return getNewKList(result);
    }

    public <N, A,B, EX extends Exception> KList<N> tryMapEachBy(ThreeArgsFunctionWithEx<? super T,N, A,B, EX> meth, A arg1, B arg2) {

        List<N> result = new ArrayList<>(getJList().size());

        getJList().forEach(element -> {
            try {
                result.add(meth.apply(element, arg1, arg2));
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });
        return getNewKList(result);
    }

    public <N, A,B,C, EX extends Exception> KList<N> tryMapEachBy(FourArgsFunctionWithEx<? super T,N, A,B,C, EX> meth, A arg1, B arg2, C arg3) {

        List<N> result = new ArrayList<>(getJList().size());

        getJList().forEach(element -> {
            try {
                result.add(meth.apply(element, arg1, arg2, arg3));
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });
        return getNewKList(result);
    }

    public <N, A,B,C,D, EX extends Exception> KList<N> tryMapEachBy(FiveArgsFunctionWithEx<? super T,N, A,B,C,D, EX> meth, A arg1, B arg2, C arg3, D arg4) {

        List<N> result = new ArrayList<>(getJList().size());

        getJList().forEach(element -> {
            try {
                result.add(meth.apply(element, arg1, arg2, arg3, arg4));
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });
        return getNewKList(result);
    }

    public <N, A,B,C,D,E, EX extends Exception> KList<N> tryMapEachBy(SixArgsFunctionWithEx<? super T,N, A,B,C,D,E, EX> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {

        List<N> result = new ArrayList<>(getJList().size());

        getJList().forEach(element -> {
            try {
                result.add(meth.apply(element, arg1, arg2, arg3, arg4, arg5));
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });
        return getNewKList(result);
    }

    //Отображение непустых полей в коллекцию данных нового типа
    public <N, EX extends Exception> KList<N> mapToNonNullsBy(OneArgFunctionWithEx<T, N, EX> meth) throws EX {

        KList<N> result = new KList<>(new ArrayList<>(getJList().size()));
        for (T element : getJList()) {
            result.add(meth.apply(element));
        }
        return result.filterNonNulls(i->i);
    }

    public <N, A, EX extends Exception> KList<N> mapToNonNullsBy(TwoArgsFunctionWithEx<? super T,N, A, EX> meth, A arg1) throws EX {

        KList<N> result = new KList<>(new ArrayList<>(getJList().size()));
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1));
        }
        return result.filterNonNulls(i->i);
    }

    public <N, A,B, EX extends Exception> KList<N> mapToNonNullsBy(ThreeArgsFunctionWithEx<? super T,N, A,B, EX> meth, A arg1, B arg2) throws EX {

        KList<N> result = new KList<>(new ArrayList<>(getJList().size()));
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1, arg2));
        }
        return result.filterNonNulls(i->i);
    }

    public <N, A,B,C, EX extends Exception> KList<N> mapToNonNullsBy(FourArgsFunctionWithEx<? super T,N, A,B,C, EX> meth, A arg1, B arg2, C arg3) throws EX {

        KList<N> result = new KList<>(new ArrayList<>(getJList().size()));
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1, arg2, arg3));
        }
        return result.filterNonNulls(i->i);
    }

    public <N, A,B,C,D, EX extends Exception> KList<N> mapToNonNullsBy(FiveArgsFunctionWithEx<? super T,N, A,B,C,D, EX> meth, A arg1, B arg2, C arg3, D arg4) throws EX {

        KList<N> result = new KList<>(new ArrayList<>(getJList().size()));
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1, arg2, arg3, arg4));
        }
        return result.filterNonNulls(i->i);
    }

    public <N, A,B,C,D,E, EX extends Exception> KList<N> mapToNonNullsBy(SixArgsFunctionWithEx<? super T,N, A,B,C,D,E, EX> meth, A arg1, B arg2, C arg3, D arg4, E arg5) throws EX {

        KList<N> result = new KList<>(new ArrayList<>(getJList().size()));
        for (T element : getJList()) {
            result.add(meth.apply(element, arg1, arg2, arg3, arg4, arg5));
        }
        return result.filterNonNulls(i->i);
    }

    //Обработка списка целиком (не умеет в проверяемые исключения)

    //Передача списка в метод, не возвращающий значения
    public KList<T> withCollectionCatchMethod(CollectionCatchMethod<KList<T>> catchMethod) {
        this.catchMethod = catchMethod;
        return getCurrentKList();
    }

    public <EX extends Exception> KList<T> useWholeBy(ConsumerMethodWithEx<KList<T>, EX> meth) throws EX {
        meth.apply(getCurrentKList());
        return getCurrentKList();
    }

    public <A, EX extends Exception> KList<T> useWholeBy(TwoArgsConsumerMethodWithEx<KList<T>, A, EX> meth, A arg1) throws EX {
        meth.apply(getCurrentKList(), arg1);
        return getCurrentKList();
    }

    public <A,B, EX extends Exception> KList<T> useWholeBy(ThreeArgsConsumerMethodWithEx<KList<T>, A,B, EX> meth, A arg1, B arg2) throws EX {
        meth.apply(getCurrentKList(), arg1, arg2);
        return getCurrentKList();
    }

    public <A,B,C, EX extends Exception> KList<T> useWholeBy(FourArgsConsumerMethodWithEx<KList<T>, A,B,C, EX> meth, A arg1, B arg2, C arg3) throws EX {
        meth.apply(getCurrentKList(), arg1, arg2, arg3);
        return getCurrentKList();
    }

    public <A,B,C,D, EX extends Exception> KList<T> useWholeBy(FiveArgsConsumerMethodWithEx<KList<T>, A,B,C,D, EX> meth, A arg1, B arg2, C arg3, D arg4) throws EX {
        meth.apply(getCurrentKList(), arg1, arg2, arg3, arg4);
        return getCurrentKList();
    }

    public <A,B,C,D,E, EX extends Exception> KList<T> useWholeBy(SixArgsConsumerMethodWithEx<KList<T>, A,B,C,D,E, EX> meth, A arg1, B arg2, C arg3, D arg4, E arg5) throws EX {
        meth.apply(getCurrentKList(), arg1, arg2, arg3, arg4, arg5);
        return getCurrentKList();
    }

    //Передача списка в метод, не возвращающий значения + try/catch
    public <EX extends Exception> KList<T> tryUseWholeBy(ConsumerMethodWithEx<KList<T>, EX> meth) {
        try {
            meth.apply(getCurrentKList());
        } catch (Exception e) {
            catchMethod.apply(getCurrentKList(), e);
        }
        return getCurrentKList();
    }

    public <A, EX extends Exception> KList<T> tryUseWholeBy(TwoArgsConsumerMethodWithEx<KList<T>, A, EX> meth, A arg1) {
        try {
            meth.apply(getCurrentKList(), arg1);
        } catch (Exception e) {
            catchMethod.apply(getCurrentKList(), e);
        }
        return getCurrentKList();
    }

    public <A,B, EX extends Exception> KList<T> tryUseWholeBy(ThreeArgsConsumerMethodWithEx<KList<T>, A,B, EX> meth, A arg1, B arg2) {
        try {
            meth.apply(getCurrentKList(), arg1, arg2);
        } catch (Exception e) {
            catchMethod.apply(getCurrentKList(), e);
        }
        return getCurrentKList();
    }

    public <A,B,C, EX extends Exception> KList<T> tryUseWholeBy(FourArgsConsumerMethodWithEx<KList<T>, A,B,C, EX> meth, A arg1, B arg2, C arg3) {
        try {
            meth.apply(getCurrentKList(), arg1, arg2, arg3);
        } catch (Exception e) {
            catchMethod.apply(getCurrentKList(), e);
        }
        return getCurrentKList();
    }

    public <A,B,C,D, EX extends Exception> KList<T> tryUseWholeBy(FiveArgsConsumerMethodWithEx<KList<T>, A,B,C,D, EX> meth, A arg1, B arg2, C arg3, D arg4) {
        try {
            meth.apply(getCurrentKList(), arg1, arg2, arg3, arg4);
        } catch (Exception e) {
            catchMethod.apply(getCurrentKList(), e);
        }
        return getCurrentKList();
    }

    public <A,B,C,D,E, EX extends Exception> KList<T> tryUseWholeBy(SixArgsConsumerMethodWithEx<KList<T>, A,B,C,D,E, EX> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {
        try {
            meth.apply(getCurrentKList(), arg1, arg2, arg3, arg4, arg5);
        } catch (Exception e) {
            catchMethod.apply(getCurrentKList(), e);
        }
        return getCurrentKList();
    }

    //Отображение всего списка во что-угодно
    public <N, EX extends Exception> N mapWholeBy(OneArgFunctionWithEx<KList<T>,N, EX> meth) throws EX {
        return meth.apply(getCurrentKList());
    }

    public <N, A, EX extends Exception> N mapWholeBy(TwoArgsFunctionWithEx<KList<T>,N, A, EX> meth, A arg1) throws EX {
        return meth.apply(getCurrentKList(), arg1);
    }

    public <N, A,B, EX extends Exception> N mapWholeBy(ThreeArgsFunctionWithEx<KList<T>,N, A,B, EX> meth, A arg1, B arg2) throws EX {
        return meth.apply(getCurrentKList(), arg1, arg2);
    }

    public <N, A,B,C, EX extends Exception> N mapWholeBy(FourArgsFunctionWithEx<KList<T>,N, A,B,C, EX> meth, A arg1, B arg2, C arg3) throws EX {
        return meth.apply(getCurrentKList(), arg1, arg2, arg3);
    }

    public <N, A,B,C,D, EX extends Exception> N mapWholeBy(FiveArgsFunctionWithEx<KList<T>,N, A,B,C,D, EX> meth, A arg1, B arg2, C arg3, D arg4) throws EX {
        return meth.apply(getCurrentKList(), arg1, arg2, arg3, arg4);
    }

    public <N, A,B,C,D,E, EX extends Exception> N mapWholeBy(SixArgsFunctionWithEx<KList<T>,N, A,B,C,D,E, EX> meth, A arg1, B arg2, C arg3, D arg4, E arg5) throws EX {
        return meth.apply(getCurrentKList(), arg1, arg2, arg3, arg4, arg5);
    }


    //Отображение всего списка во что-угодно + try/catch
    public <N, EX extends Exception> N tryMapWholeBy(OneArgFunctionWithEx<KList<T>,N, EX> meth) {
        try {
            return meth.apply(getCurrentKList());
        } catch (Exception e) {
            catchMethod.apply(this, e);
        }
        return null;
    }

    public <N, A, EX extends Exception> N tryMapWholeBy(TwoArgsFunctionWithEx<KList<T>,N, A, EX> meth, A arg1) {
            try {
                return meth.apply(getCurrentKList(), arg1);
            } catch (Exception e) {
                catchMethod.apply(this, e);
            }
        return null;
    }

    public <N, A,B, EX extends Exception> N tryMapWholeBy(ThreeArgsFunctionWithEx<KList<T>,N, A,B, EX> meth, A arg1, B arg2) {
        try {
            return meth.apply(getCurrentKList(), arg1, arg2);
        } catch (Exception e) {
            catchMethod.apply(this, e);
        }
        return null;
    }

    public <N, A,B,C, EX extends Exception> N tryMapWholeBy(FourArgsFunctionWithEx<KList<T>,N, A,B,C, EX> meth, A arg1, B arg2, C arg3) {
        try {
            return meth.apply(getCurrentKList(), arg1, arg2, arg3);
        } catch (Exception e) {
            catchMethod.apply(this, e);
        }
        return null;
    }

    public <N, A,B,C,D, EX extends Exception> N tryMapWholeBy(FiveArgsFunctionWithEx<KList<T>,N, A,B,C,D, EX> meth, A arg1, B arg2, C arg3, D arg4) {
        try {
            return meth.apply(getCurrentKList(), arg1, arg2, arg3, arg4);
        } catch (Exception e) {
            catchMethod.apply(this, e);
        }
        return null;
    }

    public <N, A,B,C,D,E, EX extends Exception> N tryMapWholeBy(SixArgsFunctionWithEx<KList<T>,N, A,B,C,D,E, EX> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {
        try {
            return meth.apply(getCurrentKList(), arg1, arg2, arg3, arg4, arg5);
        } catch (Exception e) {
            catchMethod.apply(this, e);
        }
        return null;
    }

    //Извлечение данных
    public T getAny() {
        if (getCurrentKList().isEmpty()) {
            return null;
        } else {
            return getCurrentKList().get(0);
        }
    }

    public KOptional<T> getFirst() {
        if (getCurrentKList().isEmpty()) {
            return KOptional.empty();
        } else {
            return KOptional.of(getCurrentKList().get(0));
        }
    }

    public <E extends Exception> T getFirstOr(E e) throws E {
        if (getCurrentKList().isEmpty()) {
            throw e;
        } else {
            return getCurrentKList().get(0);
        }
    }

    //Нарезка списка
    public KList<KList<T>> split(int subListSize) {

        KList<KList<T>> result = new KList<>();

        if (subListSize >= this.size()) {
            result.add(this);
            return result;
        }

        int fullSubListAmount = this.size() / subListSize;
        int lastSubListAmount = this.size() % subListSize;

        for (int i = 0; i < fullSubListAmount; i++) {
            KList<T> subList = new KList<>(this.subList(i * subListSize, (i * subListSize + subListSize)));
            result.add(subList);
        }

        if (lastSubListAmount != 0) {
            result.add(new KList<>(this.subList(this.size() - lastSubListAmount, this.size())));
        }

        return result;
    }

    public static void main(String[] args) {
        KList<Integer> test = CollectionFactory.makeArrayList(1,2,3,4,5,6,7,8,10,11);
        System.out.println(test.split(4));
    }
}
