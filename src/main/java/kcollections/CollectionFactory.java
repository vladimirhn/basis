package kcollections;

import kcollections.functional.collmethods.*;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;

public class CollectionFactory<T, A, B, C, D, E> {

    private CollectionFactory() {
    }

    @SafeVarargs
    public static <T> KList<T> makeList(T... elements) {
        return makeArrayList(elements);
    }

    public static <T> KList<T> makeList(List<T> list) {

        KList<T> klist;

        if (list != null) {
            klist = new KList<>(list);
        } else {
            klist = new KList<>(new ArrayList<>());
        }
//        logger.debug(String.format("CollectionFactory created a KList of %d %s", klist.size(), klist.getItemsName()));

        return klist;
    }

    @SafeVarargs
    public static <T> KList<T> makeArrayList(T... elements) {
        List<T> list = new ArrayList<>(Arrays.asList(elements));
        KList<T> klist = new KList<>(list);
//        logger.debug(String.format("CollectionFactory created a KList of %d %s", klist.size(), klist.getItemsName()));

        return klist;
    }

    @SafeVarargs
    public static <T> KList<T> makeLinkedList(T... elements) {
        List<T> list = new LinkedList<>(Arrays.asList(elements));
        //        logger.debug(String.format("CollectionFactory created a KList of %d %s", klist.size(), klist.getItemsName()));

        return new KList<>(list);
    }

    //Make list from collection in one method
    public static <T> KList<T> makeListFrom(NoArgsCollectionMethod<T> meth) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply()));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A> KList<T> makeListFrom(OneArgCollectionMethod<T, A> meth, A arg1) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A, B> KList<T> makeListFrom(TwoArgsCollectionMethod<T, A, B> meth, A arg1, B arg2) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1, arg2)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A, B, C> KList<T> makeListFrom(ThreeArgsCollectionMethod<T, A, B, C> meth, A arg1, B arg2, C arg3) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1, arg2, arg3)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A, B, C, D> KList<T> makeListFrom(FourArgsCollectionMethod<T, A, B, C, D> meth, A arg1, B arg2, C arg3, D arg4) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1, arg2, arg3, arg4)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A, B, C, D, E> KList<T> makeListFrom(FiveArgsCollectionMethod<T, A, B, C, D, E> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1, arg2, arg3, arg4, arg5)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    private static <T> ArrayList<T> getArrayListFromCollection(Collection<T> collection) {
        if (collection == null || collection.size() == 0) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(collection);
        }
    }

    //Make list from collection method with withArgs() method
    OneArgCollectionMethod<T, A> meth1;
    TwoArgsCollectionMethod<T, A, B> meth2;
    ThreeArgsCollectionMethod<T, A, B, C> meth3;
    FourArgsCollectionMethod<T, A, B, C, D> meth4;
    FiveArgsCollectionMethod<T, A, B, C, D, E> meth5;

    //OneArgCollectionMethod
    private CollectionFactory(OneArgCollectionMethod<T, A> meth) {
        this.meth1 = meth;
    }

    public KList<T> withArgs(A arg1) {
        KList<T> result = new CollectionFactory<>(meth1).makeListFrom(arg1);

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth1);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> makeListFrom(OneArgCollectionMethod<T, A> meth) {
        return new CollectionFactory<>(meth);
    }

    private KList<T> makeListFrom(A arg1) {
        return new KList<>(getArrayListFromCollection(meth1.apply(arg1)));
    }

    //TwoArgsCollectionMethod
    private CollectionFactory(TwoArgsCollectionMethod<T, A, B> meth) {
        this.meth2 = meth;
    }

    public KList<T> withArgs(A arg1, B arg2) {
        KList<T> result = new CollectionFactory<>(meth2).makeListFrom(arg1, arg2);

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth1);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> makeListFrom(TwoArgsCollectionMethod<T, A, B> meth) {
        return new CollectionFactory<>(meth);
    }

    private KList<T> makeListFrom(A arg1, B arg2) {
        return new KList<>(getArrayListFromCollection(meth2.apply(arg1, arg2)));
    }

    //ThreeArgsCollectionMethod
    private CollectionFactory(ThreeArgsCollectionMethod<T, A, B, C> meth) {
        this.meth3 = meth;
    }

    public KList<T> withArgs(A arg1, B arg2, C arg3) {
        KList<T> result = new CollectionFactory<>(meth3).makeListFrom(arg1, arg2, arg3);

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth1);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> makeListFrom(ThreeArgsCollectionMethod<T, A, B, C> meth) {
        return new CollectionFactory<>(meth);
    }

    private KList<T> makeListFrom(A arg1, B arg2, C arg3) {
        return new KList<>(getArrayListFromCollection(meth3.apply(arg1, arg2, arg3)));
    }

    //FourArgsCollectionMethod
    private CollectionFactory(FourArgsCollectionMethod<T, A, B, C, D> meth) {
        this.meth4 = meth;
    }

    public KList<T> withArgs(A arg1, B arg2, C arg3, D arg4) {
        KList<T> result = new CollectionFactory<>(meth4).makeListFrom(arg1, arg2, arg3, arg4);

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth1);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> makeListFrom(FourArgsCollectionMethod<T, A, B, C, D> meth) {
        return new CollectionFactory<>(meth);
    }

    private KList<T> makeListFrom(A arg1, B arg2, C arg3, D arg4) {
        return new KList<>(getArrayListFromCollection(meth4.apply(arg1, arg2, arg3, arg4)));
    }

    //FiveArgsCollectionMethod
    private CollectionFactory(FiveArgsCollectionMethod<T, A, B, C, D, E> meth) {
        this.meth5 = meth;
    }

    public KList<T> withArgs(A arg1, B arg2, C arg3, D arg4, E arg5) {
        KList<T> result = new CollectionFactory<>(meth5).makeListFrom(arg1, arg2, arg3, arg4, arg5);

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth1);
//        processSearchResult(functor, result);
        return result;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> makeListFrom(FiveArgsCollectionMethod<T, A, B, C, D, E> meth) {
        return new CollectionFactory<>(meth);
    }

    private KList<T> makeListFrom(A arg1, B arg2, C arg3, D arg4, E arg5) {
        return new KList<>(getArrayListFromCollection(meth5.apply(arg1, arg2, arg3, arg4, arg5)));
    }

    //Make list from collection method with makeListFromOverloaded() method

    Class<T> type;
    A arg1; B arg2; C arg3; D arg4; E arg5;


    //OneArgCollectionMethod
    private CollectionFactory(Class<T> type, A arg1) {
        this.type = type;
        this.arg1 = arg1;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> withSignature(Class<T> type, A arg1) {
        return new CollectionFactory<>(type, arg1);
    }

    public KList<T> makeListFromOverloaded(OneArgCollectionMethod<T, A> meth) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }


    //TwoArgsCollectionMethod
    private CollectionFactory(Class<T> type, A arg1, B arg2) {
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> withSignature(Class<T> type, A arg1, B arg2) {
        return new CollectionFactory<>(type, arg1, arg2);
    }

    public KList<T> makeListFromOverloaded(TwoArgsCollectionMethod<T, A, B> meth) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1, arg2)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    //ThreeArgsCollectionMethod
    private CollectionFactory(Class<T> type, A arg1, B arg2, C arg3) {
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> withSignature(Class<T> type, A arg1, B arg2, C arg3) {
        return new CollectionFactory<>(type, arg1, arg2, arg3);
    }

    public KList<T> makeListFromOverloaded(ThreeArgsCollectionMethod<T, A, B, C> meth) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1, arg2, arg3)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    //FourArgsCollectionMethod
    private CollectionFactory(Class<T> type, A arg1, B arg2, C arg3, D arg4) {
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> withSignature(Class<T> type, A arg1, B arg2, C arg3, D arg4) {
        return new CollectionFactory<>(type, arg1, arg2, arg3, arg4);
    }

    public KList<T> makeListFromOverloaded(FourArgsCollectionMethod<T, A, B, C, D> meth) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1, arg2, arg3, arg4)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    //FiveArgsCollectionMethod
    private CollectionFactory(Class<T> type, A arg1, B arg2, C arg3, D arg4, E arg5) {
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
        this.arg5 = arg5;
    }

    public static <T, A, B, C, D, E> CollectionFactory<T, A, B, C, D, E> withSignature(Class<T> type, A arg1, B arg2, C arg3, D arg4, E arg5) {
        return new CollectionFactory<>(type, arg1, arg2, arg3, arg4, arg5);
    }

    public KList<T> makeListFromOverloaded(FiveArgsCollectionMethod<T, A, B, C, D, E> meth) {
        KList<T> result = new KList<>(getArrayListFromCollection(meth.apply(arg1, arg2, arg3, arg4, arg5)));

//        Class<?> functor = ClassTools.extractClassFromMethodReference(meth);
//        processSearchResult(functor, result);
        return result;
    }

    //Поиск по фильтрам
    /*
    private KaliteroService<T, ?> service;
    private List<Filter> filters = new LinkedList<>();

    private <Z extends Serializable> CollectionFactory(KaliteroService<T, Z> service) {
        this.service = service;
    }

    public static <T, A, B, C, D, E, Z extends Serializable> CollectionFactory<T, A, B, C, D, E> from(KaliteroService<T, Z> service) {
        return new CollectionFactory<>(service);
    }

    @SafeVarargs
    public final <V> CollectionFactory<T, A, B, C, D, E> where(BiConsumer<T, V> method, V... values) {

        checkServiceExistance();
        filters.add(new Filter<>(method, values));

        return this;
    }

    public <V> CollectionFactory<T, A, B, C, D, E> where(BiConsumer<T, V> method, List<V> values) {

        checkServiceExistance();
        filters.add(new Filter<>(method, values));

        return this;
    }

    @SafeVarargs
    public final <V> CollectionFactory<T, A, B, C, D, E> where(BiConsumer<T, V> method, SqlOperator sqlOperator, V... values) {

        checkServiceExistance();
        filters.add(new Filter<>(method, sqlOperator, values));

        return this;
    }

    public <V> CollectionFactory<T, A, B, C, D, E> where(BiConsumer<T, V> method, SqlOperator sqlOperator, Class<V> clazz) {

        checkServiceExistance();
        filters.add(new Filter<>(method, sqlOperator, clazz));

        return this;
    }

    public <V> CollectionFactory<T, A, B, C, D, E> where(Filter<T, V> filter) {

        checkServiceExistance();
        filters.add(filter);

        return this;
    }

    private void checkServiceExistance() {
        if (service == null) {
            throw new IllegalStateException("Не задан KaliteroService для поиска. " +
                    "Используйте CollectionFactory.makeListFrom(KaliteroService<T,Z> service)");
        }
    }

    @SafeVarargs
    public final <V> CollectionFactory<T, A, B, C, D, E> and(BiConsumer<T, V> method, V... values) {
        return where(method, values);
    }

    public <V> CollectionFactory<T, A, B, C, D, E> and(BiConsumer<T, V> method, List<V> values) {
        return where(method, values);
    }

    @SafeVarargs
    public final <V> CollectionFactory<T, A, B, C, D, E> and(BiConsumer<T, V> method, SqlOperator sqlOperator, V... values) {
        return where(method, sqlOperator, values);
    }

    public <V> CollectionFactory<T, A, B, C, D, E> and(BiConsumer<T, V> method, SqlOperator sqlOperator, Class<V> clazz) {
        return where(method, sqlOperator, clazz);
    }

    public <V> CollectionFactory<T, A, B, C, D, E> and(Filter<T, V> filter) {
        return where(filter);
    }

    @SafeVarargs
    public final <V> CollectionFactory<T, A, B, C, D, E> or(BiConsumer<T, V> method, V... values) {

        checkServiceExistance();
        filters.add(new FilterOr<>(method, values));

        return this;
    }

    public <V> CollectionFactory<T, A, B, C, D, E> or(BiConsumer<T, V> method, List<V> values) {

        checkServiceExistance();
        filters.add(new FilterOr<>(method, values));

        return this;
    }

    @SafeVarargs
    public final <V> CollectionFactory<T, A, B, C, D, E> or(BiConsumer<T, V> method, SqlOperator sqlOperator, V... values) {

        checkServiceExistance();
        filters.add(new FilterOr<>(method, sqlOperator, values));

        return this;
    }

    public <V> CollectionFactory<T, A, B, C, D, E> or(BiConsumer<T, V> method, SqlOperator sqlOperator, Class<V> clazz) {

        checkServiceExistance();
        filters.add(new FilterOr<>(method, sqlOperator, clazz));

        return this;
    }

    public <V> CollectionFactory<T, A, B, C, D, E> or(FilterOr<T, V> filter) {

        checkServiceExistance();
        filters.add(filter);

        return this;
    }

    public KList<T> fetch() {
        checkFiltersExistance();
        KList<T> result = makeList(service.search(filters.toArray(new Filter[0])));

        processSearchResult(service, result);
        return result;
    }

    public KList<T> fetch(int limit) {
        checkFiltersExistance();
        KList<T> result = makeList(service.search(limit, filters.toArray(new Filter[0])));

        processSearchResult(service, result);
        return result;
    }

    private void checkFiltersExistance() {
        if (filters.isEmpty()) {
            throw new IllegalStateException("Не заданы фильтры для поиска. " +
                    "Используйте методы where() и and(), чтобы установить предикаты поиска");
        }
    }

    private static void processSearchResult(Class<?> functor, KCollection<?, ?, ?> result) {

        if (functor != null) {

            if (KaliteroService.class.isAssignableFrom(functor)) {
                KaliteroService<?, ?> service = (KaliteroService<?, ?>) ContextProvider.getContext().getBean(functor);
                processSearchResult(service, result);

            } else {
                logger.debug(String.format("CollectionFactory produced a collection of %d items from %s", result.size(), functor.getSimpleName()));
            }

        } else {
            logger.debug(String.format("CollectionFactory produced a collection of %d items", result.size()));
        }
    }

    private static void processSearchResult(KaliteroService<?, ?> service, KCollection<?, ?, ?> result) {
        result.itemsName = service.getModelClass().getAnnotation(Table.class).name();
        result.serviceName = service.getClass().getSimpleName().split("\\$")[0];
        logger.debug(String.format("%s found %d entries in %s", result.serviceName, result.size(), result.itemsName));
    }
    */
}
