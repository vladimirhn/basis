package kpersistence.modelsMaster.mapping;

import kpersistence.annotations.Column;

import java.lang.reflect.Field;

public class TableModelForAllDataMapping extends TableModelForMapping {

    public TableModelForAllDataMapping(Class<?> mainClass) {
        super(mainClass);
    }

    protected boolean pickFieldCondition(Field field) {
        return field.isAnnotationPresent(Column.class);
    }
}
