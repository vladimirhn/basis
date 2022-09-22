package kpersistence.v2.modelsMaster.queries;

import kpersistence.v2.annotations.OrderBy;
import kpersistence.v2.annotations.*;
import kpersistence.v2.types.SoftDelete;
import kutils.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;

public abstract class TableModelForQueries {

    private final Class<?> klass;
    private final boolean isSoftDelete;

    private final String tableName;
    private final List<String> columns = new ArrayList<>();
    private final List<Field> orderByFields = new ArrayList<>();
    private final Map<String, Field> columnToFieldMap = new LinkedHashMap<>();
    private final Map<String, Field> fieldNameToFieldMap = new LinkedHashMap<>();
    private final Map<String, String> columnToForeignTableNameMap = new LinkedHashMap<>();
    private final Map<String, List<String>> foreignTablesColumnsMap = new LinkedHashMap<>();
    private final Map<Field, Map<String, Field>> foreignLinkFieldsToColumnToFieldMap = new LinkedHashMap<>();

    protected abstract boolean pickFieldCondition(Field field);

    public TableModelForQueries(Class<?> klass) {
        this.klass = klass;
        this.isSoftDelete = SoftDelete.class.isAssignableFrom(klass);
        this.tableName = PersistenceAnnotationsUtils.extractTableName(klass);

        List<Field> allFields = ClassUtils.getFieldsUpToObject(klass);
        allFields.forEach(field -> {
            if (pickFieldCondition(field)) {

                field.setAccessible(true);

                String columnName = field.getAnnotation(Column.class).name();
                columns.add(columnName);
                columnToFieldMap.put(columnName, field);
                fieldNameToFieldMap.put(field.getName(), field);

                if (!Objects.equals(field.getAnnotation(Column.class).foreign(), Object.class)) {
                    Class<?> foreignClass = field.getAnnotation(Column.class).foreign();
                    addForeign(foreignClass, columnName);
                }

                if (field.isAnnotationPresent(OrderBy.class)) {
                    orderByFields.add(field);
                }
            }

            if (field.isAnnotationPresent(Foreign2.class)) {
                mapForeignLinkToTargetsFields(field);
            }


        });
    }

    private void addForeign(Class<?> klass, String foreignIdColumn) {
        String foreignTableName = PersistenceAnnotationsUtils.extractTableName(klass);
        List<Field> foreignFields = ClassUtils.getFieldsUpToObject(klass);

        columnToForeignTableNameMap.put(foreignIdColumn, foreignTableName);

        List<String> foreignColumns = new ArrayList<>();

        foreignFields.forEach(field -> {
            if (pickFieldCondition(field)) {

                field.setAccessible(true);

                String foreignColumnName = field.getAnnotation(Column.class).name();
                foreignColumns.add(foreignColumnName);
            }
        });
        foreignTablesColumnsMap.put(foreignTableName, foreignColumns);
    }

    private void mapForeignLinkToTargetsFields(Field field) {
        field.setAccessible(true);
        Class<?> foreignClass = field.getType();

        Map<String, Field> foreignColumnToFieldMap = new LinkedHashMap<>();

        ClassUtils.getFieldsUpToObject(foreignClass).forEach(foreignField -> {
            if (pickFieldCondition(field)) {
                foreignField.setAccessible(true);

                String foreignColumn = foreignField.getAnnotation(Column.class).name();
                foreignColumnToFieldMap.put(foreignColumn, foreignField);
            }
        });

        foreignLinkFieldsToColumnToFieldMap.put(field, foreignColumnToFieldMap);
    }

    public Class<?> getKlass() {
        return this.klass;
    }

    public boolean isSoftDelete() {
        return isSoftDelete;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Field> getOrderByFields() {
        return orderByFields;
    }

    public Map<String, String> getColumnToForeignTableNameMap() {
        return columnToForeignTableNameMap;
    }

    public Map<String, List<String>> getForeignTablesColumnsMap() {
        return foreignTablesColumnsMap;
    }

    public Map<String, Field> getColumnToFieldMap() {
        return columnToFieldMap;
    }

    public Map<String, Field> getFieldNameToFieldMap() {
        return fieldNameToFieldMap;
    }

    public Map<Field, Map<String, Field>> getForeignLinkFieldsToColumnToFieldMap() {
        return foreignLinkFieldsToColumnToFieldMap;
    }
}
