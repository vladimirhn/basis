package kpersistence.v1.kfilters;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class Filter<T, V> {

    protected BiConsumer<T, V> method;
    protected V[] values;
    protected SqlOperator sqlOperator;
    protected Class<V> clazz;

    protected boolean isOrFilter = false;

    public Filter(Filter<T, V> orig, V[] values) {
        this(orig.method, orig.sqlOperator, values);
    }

    @SafeVarargs
    public Filter(BiConsumer<T, V> method, V... values) {

        checkArgs(method, values);
        this.method = method;
        this.values = values;
        if (values.length == 1) {
            this.sqlOperator = SqlOperator.EQUALS;
        } else {
            this.sqlOperator = SqlOperator.IN;
        }
    }

    public Filter(BiConsumer<T, V> method, List<V> values) {

        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list is null or empty");
        }
        if (method == null) {
            throw new IllegalArgumentException("Method is null");
        }

        Set<V> uniques = new HashSet<>(values);

        this.method = method;
        this.values = (V[]) Array.newInstance(values.get(0).getClass(), values.size());

        int j = 0;
        for (V value : uniques) {
            this.values[j++] = value;
        }

        if (values.size() == 1) {
            this.sqlOperator = SqlOperator.EQUALS;
        } else {
            this.sqlOperator = SqlOperator.IN;
        }
    }

    @SafeVarargs
    public Filter(BiConsumer<T, V> method, SqlOperator sqlOperator, V... values) {
        checkArgs(method, values);
        this.method = method;
        this.values = values;
        this.sqlOperator = sqlOperator;
    }

    public Filter(BiConsumer<T, V> method, SqlOperator sqlOperator, Class<V> clazz) {

        checkArgsForNullFilters(method, sqlOperator);

        this.method = method;
        this.sqlOperator = sqlOperator;
        this.clazz = clazz;
    }

    private void checkArgs(BiConsumer<T, V> method, V[] values) {
        if (method == null || values == null || values.length == 0) {
            throw new IllegalArgumentException("Образец фильтра: new Filter<>(ClassName::setValueMeth, val1, val1, val3) " +
                    "или new Filter<>(ClassName::setValueMeth, SqlOperator.OPERATOR, val1, val2, val3)");
        }
    }

    private void checkArgsForNullFilters(BiConsumer<T, V> method, SqlOperator sqlOperator) {
        boolean isBadMethod = method == null;
        boolean isBadOperator = sqlOperator != SqlOperator.IS_NULL && sqlOperator != SqlOperator.IS_NOT_NULL;

        if (isBadMethod || isBadOperator) {
            throw new IllegalArgumentException("Образец фильтра на IS NULL и IS NOT NULL: " +
                    "new Filter<>(ClassName::setValueMeth, SqlOperator.IS_NULL, ValueType.class) ");
        }
    }

    public Object setUpLabel(T obj) {

        V label;

        if (sqlOperator == SqlOperator.IS_NULL || sqlOperator == SqlOperator.IS_NOT_NULL) {
            label = getNewInstance(this.clazz);

        } else {
            label = getNewInstance((Class<V>) this.values[0].getClass());
        }

        method.accept(obj, label);

        return label;
    }

    private V getNewInstance(Class<V> clazz) {
        //Типы без пустого конструктора (Boolean, Integer, Long, Double, Float)
        if (clazz.equals(Boolean.class)) {
            return (V) Boolean.TRUE;

        } else if (clazz.equals(Integer.class)) {
            return (V) Integer.valueOf(0);

        } else if (clazz.equals(Long.class)) {
            return (V) Long.valueOf(0L);

        } else if (clazz.equals(Double.class)) {
            return (V) new Double(0D);

        } else if (clazz.equals(Float.class)) {
            return (V) new Float(0F);

        } else { //Типы с  конструктором без параметров (Date, String)
            try {
                return (V) clazz.newInstance();

            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException("Не удалось создать второй экземпляр класса значения фильтра для метки.");
            }
        }
    }

    public Object getValues() {

        //Проработка операторов IS NULL и IS NOT NULL
        if (sqlOperator == SqlOperator.IS_NULL || sqlOperator == SqlOperator.IS_NOT_NULL) {
            return ""; //Значение ка таковое не требуется
        }

        //Проработка оператора LIKE
        if (sqlOperator == SqlOperator.LIKE || sqlOperator == SqlOperator._LIKE || sqlOperator == SqlOperator.LIKE_) {
            if (values[0] instanceof String) {

                String stringValue = (String) values[0];

                if (sqlOperator == SqlOperator.LIKE) {
                    return "%" + stringValue + "%";

                } else if (sqlOperator == SqlOperator._LIKE) {
                    return "%" + stringValue;

                } else {
                    return stringValue + "%";
                }

            } else {
                throw new IllegalArgumentException("SqlOperator LIKE работает только со строками");
            }
        }

        //Если в массиве одно значние, его нужно излечь
        if (values.length == 1) {
            return replaceBooleanValues(values)[0];
        }
        //Если значений больше,  возвращаем массив целиком
        return Arrays.asList(replaceBooleanValues(values));
    }

    public SqlOperator getSqlOperator() {
        return sqlOperator;
    }

    public V[] getValuesArray() {
        return values;
    }

    private Object[] replaceBooleanValues(Object... values) {
        if (values == null || values.length == 0) {
            return values;
        }
        if (!(values instanceof Boolean[])) {
            return values;
        }
        Integer[] newValues = new Integer[values.length];
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if ((Boolean) value) {
                newValues[i] = 1;
            } else {
                newValues[i] = 0;
            }

        }

        return newValues;
    }

    public boolean isOrFilter() {
        return isOrFilter;
    }
}
