package kpersistence.v2.modelsMaster.queries;

import kpersistence.v2.annotations.Column;

import java.lang.reflect.Field;

public class TableModelForAllDataQueries extends TableModelForQueries {

    public TableModelForAllDataQueries(Class<?> klass) {
        super(klass);
    }

    protected boolean pickFieldCondition(Field field) {
        return field.isAnnotationPresent(Column.class);
    }
}