package kpersistence.modelsMaster.queries;

import kpersistence.annotations.Column;
import kpersistence.annotations.Id;
import kpersistence.annotations.Label;

import java.lang.reflect.Field;

public class TableModelForLabelsQueries extends TableModelForQueries {

    public TableModelForLabelsQueries(Class<?> klass) {
        super(klass);
    }

    protected boolean pickFieldCondition(Field field) {
        return field.isAnnotationPresent(Column.class) &&
                (field.isAnnotationPresent(Label.class) || field.isAnnotationPresent(Id.class));
    }
}