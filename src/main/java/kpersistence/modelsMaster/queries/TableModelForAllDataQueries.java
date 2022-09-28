package kpersistence.modelsMaster.queries;

import kpersistence.annotations.Column;

import java.lang.reflect.Field;

public class TableModelForAllDataQueries extends TableModelForQueries {

    public TableModelForAllDataQueries(Class<?> klass) {
        super(klass);
    }

    protected boolean pickFieldCondition(Field field) {
        return field.isAnnotationPresent(Column.class);
    }
}