package kpersistence.v1.query;

import kcollections.CollectionFactory;
import kcollections.KList;
import kpersistence.v1.kfilters.SqlOperator;
import kpersistence.v2.annotations.Column;
import kutils.ClassUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class KFilter {

    public KList<SqlPredicate> getPredicates() {

        KList<SqlPredicate> result = CollectionFactory.makeList();

        Class<?> filterClass = this.getClass();
        Class<?> modelClass = filterClass.getEnclosingClass();

        List<Field> filterFields = ClassUtils
                .getFields(filterClass).stream()
                .filter(this::nonNullField)
                .collect(Collectors.toList());

        List<Field> modelFields = ClassUtils.getFieldsUpToObject(modelClass);

        for (Field filterField : filterFields) {
            String filterFieldName = filterField.getName();

            try {
                modelFields.stream()
                        .filter(field -> Objects.equals(field.getName(), filterFieldName))
                        .findAny()
                        .ifPresent(field -> {
                            Object filterValue = getFieldValue(filterField, this);
                            String columnName = field.getAnnotation(Column.class).name();

                            result.add(new SqlPredicate(columnName, SqlOperator.EQUALS, filterValue));
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private boolean nonNullField(Field field) {
        try {
            field.setAccessible(true);
            return field.get(this) != null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Object getFieldValue(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
