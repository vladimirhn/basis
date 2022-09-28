package kpersistence.modelsMaster.mapping;

import kpersistence.annotations.Column;
import kpersistence.annotations.Id;
import kpersistence.annotations.Label;

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
