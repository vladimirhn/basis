package kpersistence.v2.modelsMaster.mapping;

import kpersistence.v2.annotations.Column;
import kpersistence.v2.annotations.Id;
import kpersistence.v2.annotations.Label;

import java.lang.reflect.Field;

public class TableModelForLabelsMapping extends TableModelForMapping {

    public TableModelForLabelsMapping(Class<?> mainClass) {
        super(mainClass);
    }

    protected boolean pickFieldCondition(Field field) {
        return field.isAnnotationPresent(Column.class) &&
                (field.isAnnotationPresent(Label.class) || field.isAnnotationPresent(Id.class));
    }
}
