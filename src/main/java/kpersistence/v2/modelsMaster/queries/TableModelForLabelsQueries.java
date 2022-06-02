package kpersistence.v2.modelsMaster.queries;

import kpersistence.v2.annotations.Column;
import kpersistence.v2.annotations.Id;
import kpersistence.v2.annotations.Label;

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