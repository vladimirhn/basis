package kcollections;

import kcollections.functional.catchmethods.CatchMethod;
import kcollections.functional.consumermethods.*;
import kcollections.functional.getters.DoubleSupplier;
import kcollections.functional.getters.IntegerSupplier;
//import ro.kalite.entity.AbstractTb;
//import ro.kalite.entity.Table;
//import ro.kalite.logger.KaliteroLogger;
//import ro.kalite.logger.KaliteroLoggerFactory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class KCollection<K extends kcollections.KCollection<K, X, T>, X extends Collection<T>, T> extends StandartCollectionWrapper<T> {

//    private static final KaliteroLogger logger = KaliteroLoggerFactory.getLogger(kcollections.KCollection.class);

    protected abstract X getJCollection();

    protected abstract K getCurrentKCollection();

    protected abstract K getKCollectionDublicate();

    protected abstract K getKCollectionDublicate(int motherCollectionSize);

    protected abstract K getNewKCollection();

    protected abstract K getNewKCollection(Collection data);

    protected abstract K streamToNewKCollectionWithMotherSize(Stream<T> stream);

    Integer motherCollectionSize = null;
    CatchMethod<T> errorProcessor = null;

    String itemsName = "ITEMS";
    String serviceName = "UnknownService";

    public String getItemsName() {
        if (getCurrentKCollection() != null && !getCurrentKCollection().isEmpty() && getCurrentKCollection().iterator().hasNext()) {
            T item = getCurrentKCollection().iterator().next();
//            if (item instanceof AbstractTb) {
//                this.itemsName = item.getClass().getAnnotation(Table.class).name();
//            } else {
                this.itemsName = item.getClass().getSimpleName().toUpperCase();
//            }
        }
        return this.itemsName;
    }

    public boolean isNotEmpty() {
        return !getJCollection().isEmpty();
    }

    //plus(), minus()
    public K minus(Collection<T> collection) {
        K initColl = getKCollectionDublicate();
        initColl.removeAll(collection);
        return initColl;
    }

    public K minus(T... elements) {
        K initColl = getKCollectionDublicate();
        initColl.removeAll(Arrays.asList(elements));
        return initColl;
    }

    public K plus(Collection<T> collection) {
        K initColl = getKCollectionDublicate();
        initColl.addAll(collection);
        return initColl;
    }

    public K plus(T... elements) {
        K initColl = getKCollectionDublicate();
        initColl.addAll(Arrays.asList(elements));
        return initColl;
    }

    //Фильтрация
    //Простой предикат
    public K filter(Predicate<T> filter) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(filter)
        );
    }

    //Фильтрация по нулл/не нулл полям
    public K filterNulls(Function<? super T, ?> getter) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> getter.apply(item) == null || getter.apply(item).equals(""))
        );
    }

    public K filterNonNulls(Function<T, ?> getter) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> getter.apply(item) != null && !getter.apply(item).equals(""))
        );
    }

    //Фильтрация по Boolean полям

    /**
     * Возвращает true, если геттер вернёт true. Вернёт false, если геттер вернёт null или false
     */
    public K filterTrue(Function<T, Boolean> getter) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> getter.apply(item) != null && getter.apply(item))
        );
    }

    /**
     * Возвращает true, если геттер вернёт false. Вернёт false, если геттер вернёт null или true
     */
    public K filterFalse(Function<T, Boolean> getter) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> getter.apply(item) != null && !getter.apply(item))
        );
    }

    /**
     * Возвращает true, если геттер вернёт null или false. Вернёт false, если геттер вернёт true
     */
    public K filterNotTrue(Function<T, Boolean> getter) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> getter.apply(item) == null || !getter.apply(item))
        );
    }

    /**
     * Возвращает true, если геттер вернёт null или true. Вернёт false, если геттер вернёт false
     */
    public K filterNotFalse(Function<T, Boolean> getter) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> getter.apply(item) == null || getter.apply(item))
        );
    }

    //Фильтрация по равенству/неравенству и вхождению в списки

    /**
     * Вернёт пустой список, если value окажется null. Но исключения не выбросит.
     */
    public <V> K filterEquals(Function<T, V> getter, V value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && value.equals(getter.apply(item)))
        );
    }

    /**
     * Вернёт пустой список, если value окажется null. Но исключения не выбросит.
     */
    public <V> K filterNotEquals(Function<T, V> getter, V value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && !value.equals(getter.apply(item)))
        );
    }

    /**
     * Вернёт пустой список, если будет передан null или не передан список значений (пустой массив).
     */
    @SafeVarargs
    public final <V> K filterIn(Function<T, V> getter, V... values) {

        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> {
                    return values != null &&
                            Arrays.stream(values).anyMatch(value -> value.equals(getter.apply(item)));
                })
        );
    }

    /**
     * Вернёт пустой список, если будет передан null или не передан список значений (пустой массив).
     */
    @SafeVarargs
    public final <V> K filterNotIn(Function<T, V> getter, V... values) {

        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> {
                    return values != null &&
                            Arrays.stream(values).noneMatch(value -> value.equals(getter.apply(item)));
                })
        );
    }

    //Фильтрация по >,>=,<,<=, ==, !=
    private enum Operator {MORE, MORE_EQUALS, LESS, LESS_EQUALS, EQUALS, NOT_EQUALS}
    
    //Integer
    public K filterLeftGreaterThanRight(Function<T, Integer> getter, Integer value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && getter.apply(item) != null && getter.apply(item) > value)
        );
    }

    public K filterLeftGreaterOrEqualsRight(Function<T, Integer> getter, Integer value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && getter.apply(item) != null && getter.apply(item) >= value)
        );
    }

    public K filterLeftLessThanRight(Function<T, Integer> getter, Integer value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && getter.apply(item) != null && getter.apply(item) < value)
        );
    }

    public K filterLeftLessOrEqualsRight(Function<T, Integer> getter, Integer value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && getter.apply(item) != null && getter.apply(item) <= value)
        );
    }

    //Double
    public K filterLeftGreaterThanRight(Function<T, Double> getter, Double value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && getter.apply(item) != null && getter.apply(item) > value)
        );
    }

    public K filterLeftGreaterOrEqualsRight(Function<T, Double> getter, Double value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && getter.apply(item) != null && getter.apply(item) >= value)
        );
    }

    public K filterLeftLessThanRight(Function<T, Double> getter, Double value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && getter.apply(item) != null && getter.apply(item) < value)
        );
    }

    public K filterLeftLessOrEqualsRight(Function<T, Double> getter, Double value) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> value != null && getter.apply(item) != null && getter.apply(item) <= value)
        );
    }

    //Function and Function
    public K filterLeftGreaterThanRight(IntegerSupplier<T> getter1, IntegerSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareIntegers(getter1, getter2, item, Operator.MORE))
        );
    }

    public K filterLeftGreaterOrEqualsRight(IntegerSupplier<T> getter1, IntegerSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareIntegers(getter1, getter2, item, Operator.MORE_EQUALS))
        );
    }

    public K filterLeftLessThanRight(IntegerSupplier<T> getter1, IntegerSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareIntegers(getter1, getter2, item, Operator.LESS))
        );
    }

    public K filterLeftLessOrEqualsRight(IntegerSupplier<T> getter1, IntegerSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareIntegers(getter1, getter2, item, Operator.LESS_EQUALS))
        );
    }

    public K filterEquals(IntegerSupplier<T> getter1, IntegerSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareIntegers(getter1, getter2, item, Operator.EQUALS))
        );
    }

    public K filterNotEquals(IntegerSupplier<T> getter1, IntegerSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareIntegers(getter1, getter2, item, Operator.NOT_EQUALS))
        );
    }


    private boolean compareIntegers(IntegerSupplier<T> getter1, IntegerSupplier<T> getter2, T item, Operator operator) {
        Integer i1 = getter1.getInteger(item);
        Integer i2 = getter2.getInteger(item);

        if (i1 == null || i2 == null) {
            return false;
        } else {
            switch (operator) {
                case MORE:
                    return i1 > i2;
                case MORE_EQUALS:
                    return i1 >= i2;
                case LESS:
                    return i1 < i2;
                case LESS_EQUALS:
                    return i1 <= i2;
                case EQUALS:
                    return i1.equals(i2);
                case NOT_EQUALS:
                    return !i1.equals(i2);
            }
        }
        return false;
    }

    public K filterLeftGreaterThanRight(DoubleSupplier<T> getter1, DoubleSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareDoubles(getter1, getter2, item, Operator.MORE))
        );
    }

    public K filterLeftGreaterOrEqualsRight(DoubleSupplier<T> getter1, DoubleSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareDoubles(getter1, getter2, item, Operator.MORE_EQUALS))
        );
    }

    public K filterLeftLessThanRight(DoubleSupplier<T> getter1, DoubleSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareDoubles(getter1, getter2, item, Operator.LESS))
        );
    }

    public K filterLeftLessOrEqualsRight(DoubleSupplier<T> getter1, DoubleSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareDoubles(getter1, getter2, item, Operator.LESS_EQUALS))
        );
    }

    public K filterEquals(DoubleSupplier<T> getter1, DoubleSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareDoubles(getter1, getter2, item, Operator.EQUALS))
        );
    }

    public K filterNotEquals(DoubleSupplier<T> getter1, DoubleSupplier<T> getter2) {
        return streamToNewKCollectionWithMotherSize(
                getJCollection().stream().filter(item -> compareDoubles(getter1, getter2, item, Operator.NOT_EQUALS))
        );
    }

    private boolean compareDoubles(DoubleSupplier<T> getter1, DoubleSupplier<T> getter2, T item, Operator operator) {
        Double d1 = getter1.getDouble(item);
        Double d2 = getter2.getDouble(item);

        if (d1 == null || d2 == null) {
            return false;
        } else {
            switch (operator) {
                case MORE:
                    return d1 > d2;
                case MORE_EQUALS:
                    return d1 >= d2;
                case LESS:
                    return d1 < d2;
                case LESS_EQUALS:
                    return d1 <= d2;
                case EQUALS:
                    return d1.equals(d2);
                case NOT_EQUALS:
                    return !d1.equals(d2);
            }
        }
        return false;
    }

    //Уникализация (distinct)
    @SafeVarargs
    public final K distinct(Function<T, ?>... getters) {

        if (getJCollection().isEmpty()) {
            return streamToNewKCollectionWithMotherSize(getJCollection().stream());
        }

        LinkedList<T> uniques = new LinkedList<>();
        getJCollection().forEach(entry -> {
            if (uniques.stream().noneMatch(unique -> Arrays.deepEquals(applyGetters(entry, getters), applyGetters(unique, getters)))) {
                uniques.add(entry);
            }
        });

        return streamToNewKCollectionWithMotherSize(uniques.stream());
    }

    private Object[] applyGetters(Object o, Function[] getters) {
        Object[] result = new Object[getters.length];
        for (int i = 0; i < getters.length; i++) {
            result[i] = getters[i].apply(o);
        }
        return result;
    }

    //Проверки
    //Простые проверки, без действий
    public boolean nonePassed() {
        checkIfAfterFilter();
        return getJCollection().size() == 0;
    }

    public boolean somePassed() {
        checkIfAfterFilter();
        return getJCollection().size() > 0;
    }

    public boolean somePassedSomeNot() {
        checkIfAfterFilter();
        return getJCollection().size() > 0 && getJCollection().size() < motherCollectionSize;
    }

    public boolean someNotPassed() {
        checkIfAfterFilter();
        return getJCollection().size() < motherCollectionSize;
    }

    public boolean allPassed() {
        checkIfAfterFilter();
        return getJCollection().size() == motherCollectionSize;
    }

    //Проверки с вызовом произвольного метода (Consumer)
    public <A> K ifNonePassed(ConsumerMethod<A> meth, A arg1) {
        if (nonePassed()) meth.apply(arg1);
        return getCurrentKCollection();
    }

    public <A, B> K ifNonePassed(TwoArgsConsumerMethod<A,B> meth, A arg1, B arg2) {
        if (nonePassed()) meth.apply(arg1, arg2);
        return getCurrentKCollection();
    }

    public <A, B, C> K ifNonePassed(ThreeArgsConsumerMethod<A, B, C> meth, A arg1, B arg2, C arg3) {
        if (nonePassed()) meth.apply(arg1, arg2, arg3);
        return getCurrentKCollection();
    }

    public <A, B, C, D> K ifNonePassed(FourArgsConsumerMethod<A, B, C, D> meth, A arg1, B arg2, C arg3, D arg4) {
        if (nonePassed()) meth.apply(arg1, arg2, arg3, arg4);
        return getCurrentKCollection();
    }

    public <A, B, C, D, E> K ifNonePassed(FiveArgsConsumerMethod<A, B, C, D, E> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {
        if (nonePassed()) meth.apply(arg1, arg2, arg3, arg4, arg5);
        return getCurrentKCollection();
    }

    public <A> K ifSomePassed(ConsumerMethod<A> meth, A arg1) {
        if (somePassed()) meth.apply(arg1);
        return getCurrentKCollection();
    }

    public <A, B> K ifSomePassed(TwoArgsConsumerMethod<A,B> meth, A arg1, B arg2) {
        if (somePassed()) meth.apply(arg1, arg2);
        return getCurrentKCollection();
    }

    public <A, B, C> K ifSomePassed(ThreeArgsConsumerMethod<A, B, C> meth, A arg1, B arg2, C arg3) {
        if (somePassed()) meth.apply(arg1, arg2, arg3);
        return getCurrentKCollection();
    }

    public <A, B, C, D> K ifSomePassed(FourArgsConsumerMethod<A, B, C, D> meth, A arg1, B arg2, C arg3, D arg4) {
        if (somePassed()) meth.apply(arg1, arg2, arg3, arg4);
        return getCurrentKCollection();
    }

    public <A, B, C, D, E> K ifSomePassed(FiveArgsConsumerMethod<A, B, C, D, E> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {
        if (somePassed()) meth.apply(arg1, arg2, arg3, arg4, arg5);
        return getCurrentKCollection();
    }

    public <A> K ifSomePassedSomeNot(ConsumerMethod<A> meth, A arg1) {
        if (somePassedSomeNot()) meth.apply(arg1);
        return getCurrentKCollection();
    }

    public <A, B> K ifSomePassedSomeNot(TwoArgsConsumerMethod<A,B> meth, A arg1, B arg2) {
        if (somePassedSomeNot()) meth.apply(arg1, arg2);
        return getCurrentKCollection();
    }

    public <A, B, C> K ifSomePassedSomeNot(ThreeArgsConsumerMethod<A, B, C> meth, A arg1, B arg2, C arg3) {
        if (somePassedSomeNot()) meth.apply(arg1, arg2, arg3);
        return getCurrentKCollection();
    }

    public <A, B, C, D> K ifSomePassedSomeNot(FourArgsConsumerMethod<A, B, C, D> meth, A arg1, B arg2, C arg3, D arg4) {
        if (somePassedSomeNot()) meth.apply(arg1, arg2, arg3, arg4);
        return getCurrentKCollection();
    }

    public <A, B, C, D, E> K ifSomePassedSomeNot(FiveArgsConsumerMethod<A, B, C, D, E> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {
        if (somePassedSomeNot()) meth.apply(arg1, arg2, arg3, arg4, arg5);
        return getCurrentKCollection();
    }

    public <A> K ifSomeNotPassed(ConsumerMethod<A> meth, A arg1) {
        if (someNotPassed()) meth.apply(arg1);
        return getCurrentKCollection();
    }

    public <A, B> K ifSomeNotPassed(TwoArgsConsumerMethod<A,B> meth, A arg1, B arg2) {
        if (someNotPassed()) meth.apply(arg1, arg2);
        return getCurrentKCollection();
    }

    public <A, B, C> K ifSomeNotPassed(ThreeArgsConsumerMethod<A, B, C> meth, A arg1, B arg2, C arg3) {
        if (someNotPassed()) meth.apply(arg1, arg2, arg3);
        return getCurrentKCollection();
    }

    public <A, B, C, D> K ifSomeNotPassed(FourArgsConsumerMethod<A, B, C, D> meth, A arg1, B arg2, C arg3, D arg4) {
        if (someNotPassed()) meth.apply(arg1, arg2, arg3, arg4);
        return getCurrentKCollection();
    }

    public <A, B, C, D, E> K ifSomeNotPassed(FiveArgsConsumerMethod<A, B, C, D, E> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {
        if (someNotPassed()) meth.apply(arg1, arg2, arg3, arg4, arg5);
        return getCurrentKCollection();
    }

    public <A> K ifAllPassed(ConsumerMethod<A> meth, A arg1) {
        if (allPassed()) meth.apply(arg1);
        return getCurrentKCollection();
    }

    public <A, B> K ifAllPassed(TwoArgsConsumerMethod<A,B> meth, A arg1, B arg2) {
        if (allPassed()) meth.apply(arg1, arg2);
        return getCurrentKCollection();
    }

    public <A, B, C> K ifAllPassed(ThreeArgsConsumerMethod<A, B, C> meth, A arg1, B arg2, C arg3) {
        if (allPassed()) meth.apply(arg1, arg2, arg3);
        return getCurrentKCollection();
    }

    public <A, B, C, D> K ifAllPassed(FourArgsConsumerMethod<A, B, C, D> meth, A arg1, B arg2, C arg3, D arg4) {
        if (allPassed()) meth.apply(arg1, arg2, arg3, arg4);
        return getCurrentKCollection();
    }

    public <A, B, C, D, E> K ifAllPassed(FiveArgsConsumerMethod<A, B, C, D, E> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {
        if (allPassed()) meth.apply(arg1, arg2, arg3, arg4, arg5);
        return getCurrentKCollection();
    }


    //Проверки с выбросом исключения
    public <E extends Exception> K ifNonePassed(E e) throws E {
        if (nonePassed()) {
            throw e;
        }
        return getCurrentKCollection();
    }

    public <E extends Exception> K ifSomePassed(E e) throws E {
        if (somePassed()) {
            throw e;
        }
        return getCurrentKCollection();
    }

    public <E extends Exception> K ifSomePassedSomeNot(E e) throws E {
        if (somePassedSomeNot()) {
            throw e;
        }
        return getCurrentKCollection();
    }

    public <E extends Exception> K ifSomeNotPassed(E e) throws E {
        if (someNotPassed()) {
            throw e;
        }
        return getCurrentKCollection();
    }

    public <E extends Exception> K ifAllPassed(E e) throws E {
        if (allPassed()) {
            throw e;
        }
        return getCurrentKCollection();
    }

    public <E extends Exception> K ifEmptyThrow(E e) throws E {
        if (isEmpty()) {
            throw e;
        }
        return getCurrentKCollection();
    }

    private void checkIfAfterFilter() {
        if (motherCollectionSize == null) {
            throw new IllegalStateException("Проверки состояния элементов коллекции проводятся только после филтров.");
        }
    }

    //Обработка элементов коллекции
    //Установка значений
    public <V> K setValue(BiConsumer<T, V> setter, V value) {
        getJCollection().forEach(element -> setter.accept(element, value));
        return getCurrentKCollection();
    }

    public <V> K setValue(BiConsumer<T, V> setter, Function<T, V> valueSupplier) {
        getJCollection().forEach(element -> setter.accept(element, valueSupplier.apply(element)));
        return getCurrentKCollection();
    }

    //Произвольная обработка
    //Без изменения типа коллекции
    //Без обработки исключений

    public <EX extends Exception> K useEachBy(ConsumerMethodWithEx<? super T, EX> meth) throws EX {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        for (T element : getJCollection()) {
            meth.apply(element);
        }
        return getCurrentKCollection();
    }

    public <A, EX extends Exception> K useEachBy(TwoArgsConsumerMethodWithEx<? super T, A, EX> meth, A arg1) throws EX {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        for (T element : getJCollection()) {
            meth.apply(element, arg1);
        }
        return getCurrentKCollection();
    }

    public <A, B, EX extends Exception> K useEachBy(ThreeArgsConsumerMethodWithEx<? super T, A, B, EX> meth, A arg1, B arg2) throws EX {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        for (T element : getJCollection()) {
            meth.apply(element, arg1, arg2);
        }
        return getCurrentKCollection();
    }

    public <A, B, C, EX extends Exception> K useEachBy(FourArgsConsumerMethodWithEx<? super T, A, B, C, EX> meth, A arg1, B arg2, C arg3) throws EX {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        for (T element : getJCollection()) {
            meth.apply(element, arg1, arg2, arg3);
        }
        return getCurrentKCollection();
    }

    public <A, B, C, D, EX extends Exception> K useEachBy(FiveArgsConsumerMethodWithEx<? super T, A, B, C, D, EX> meth, A arg1, B arg2, C arg3, D arg4) throws EX {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        for (T element : getJCollection()) {
            meth.apply(element, arg1, arg2, arg3, arg4);
        }
        return getCurrentKCollection();
    }

    public <A, B, C, D, E, EX extends Exception> K useEachBy(SixArgsConsumerMethodWithEx<? super T, A, B, C, D, E, EX> meth, A arg1, B arg2, C arg3, D arg4, E arg5) throws EX {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        for (T element : getJCollection()) {
            meth.apply(element, arg1, arg2, arg3, arg4, arg5);
        }
        return getCurrentKCollection();
    }


    public K withCatchMethod(CatchMethod<T> errorProcessor) {
        this.errorProcessor = errorProcessor;
        return getCurrentKCollection();
    }

    public <EX extends Exception> K tryUseEachBy(ConsumerMethodWithEx<? super T, EX> meth) {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        getJCollection().forEach(element -> {
            try {
                meth.apply(element);
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });

        return getCurrentKCollection();
    }

    public <A, EX extends Exception> K tryUseEachBy(TwoArgsConsumerMethodWithEx<? super T, A, EX> meth, A arg1) {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        getJCollection().forEach(element -> {
            try {
                meth.apply(element, arg1);
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });

        return getCurrentKCollection();
    }

    public <A, B, EX extends Exception> K tryUseEachBy(ThreeArgsConsumerMethodWithEx<? super T, A, B, EX> meth, A arg1, B arg2) {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        getJCollection().forEach(element -> {
            try {
                meth.apply(element, arg1, arg2);
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });

        return getCurrentKCollection();
    }

    public <A, B, C, EX extends Exception> K tryUseEachBy(FourArgsConsumerMethodWithEx<? super T, A, B, C, EX> meth, A arg1, B arg2, C arg3) {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        getJCollection().forEach(element -> {
            try {
                meth.apply(element, arg1, arg2, arg3);
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });

        return getCurrentKCollection();
    }

    public <A, B, C, D, EX extends Exception> K tryUseEachBy(FiveArgsConsumerMethodWithEx<? super T, A, B, C, D, EX> meth, A arg1, B arg2, C arg3, D arg4) {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        getJCollection().forEach(element -> {
            try {
                meth.apply(element, arg1, arg2, arg3, arg4);
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });

        return getCurrentKCollection();
    }

    public <A, B, C, D, E, EX extends Exception> K tryUseEachBy(SixArgsConsumerMethodWithEx<? super T, A, B, C, D, E, EX> meth, A arg1, B arg2, C arg3, D arg4, E arg5) {

//        logger.debug(String.format("%s is going to process %d %s", meth.getMethodName(), getCurrentKCollection().size(), getCurrentKCollection().itemsName));

        getJCollection().forEach(element -> {
            try {
                meth.apply(element, arg1, arg2, arg3, arg4, arg5);
            } catch (Exception e) {
                errorProcessor.apply(element, e);
            }
        });

        return getCurrentKCollection();
    }


    //Преобразование

    //Преобразование к отображению
    public <V> Map<V, KList<T>> groupBy(Function<T, V> getter) {
        Map<V, KList<T>> result = new HashMap<>();

        getCurrentKCollection().stream().collect(Collectors.groupingBy(getter))
                .forEach((v, ts) -> result.put(v, new KList<>(ts)));

        return result;
    }
    public <V> Map<V, KList<T>> groupByWithNulls(Function<T, V> getter) {
        Map<V, KList<T>> result = new HashMap<>();

        getCurrentKCollection().forEach(element -> {

            V key = getter.apply(element);
            if (key == null) return;

            if (!result.containsKey(key)) {
                result.put(key, new KList<>(new LinkedList<>()));
            }

            result.get(key).add(element);
        });

        return result;
    }

    //Преобразование к простому множеству (HashSet)
    public Set<T> toSet() {
        return new HashSet<>(getCurrentKCollection());
    }

    //Преобразование к строке по полю с запятой в качестве разделителя
    public <V> String toCommaString(Function<T,V> getter) {
        return getJCollection().stream()
                .map(getter)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    //Ручное логгирование
    public K log(Consumer<String> logMethod, String message) {
        if (message.contains(":size")) {
            message = message.replace(":size", String.valueOf(getCurrentKCollection().size()));
        }
        logMethod.accept(message);
        return getCurrentKCollection();
    }
}
