package kpersistence.v2.modelsMaster.schema;

import kpersistence.v2.annotations.Column;
import kpersistence.v2.annotations.ColumnType;
import kpersistence.v2.annotations.Table;
import kutils.ClassUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class TableDescription {

    Class<?> klass;
    String tableName;
    Map<String, ColumnDescription> columnDescriptions = new LinkedHashMap();

    public TableDescription(Class<?> klass) {

        if (klass.isAnnotationPresent(Table.class)) {
            this.klass = klass;
            tableName = klass.getAnnotation(Table.class).name();

            for (Field field : ClassUtils.getFieldsUpToObject(klass)) {

                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    String colName = field.getName();
                    ColumnDescription description = new ColumnDescription();

                    if (!ColumnType.ORDINARY.equals(column.type())) {
                        description.type = column.type();
                    }

                    if (column.nonNull()) {
                        description.nonNull = true;
                    }

                    columnDescriptions.put(colName, description);
                }
            }
        }
    }

    public Class<?> getKlass() {
        return klass;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, ColumnDescription> getColumnDescriptions() {
        return columnDescriptions;
    }
}
