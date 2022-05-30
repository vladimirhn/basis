package kpersistence.v2.modelsMaster;

import kpersistence.v2.annotations.Column;
import kpersistence.v2.annotations.PersistenceAnnotationsUtils;
import kutils.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;

public class TableModelForQueries {

    private String userId;

    private final String tableName;
    private final List<String> columns = new ArrayList<>();
    private final Map<String, Field> columnToFieldMap = new LinkedHashMap<>();
    private final Map<String, String> columnToParentNameMap = new LinkedHashMap<>();
    private final Map<String, List<String>> parentTablesColumnsMap = new LinkedHashMap<>();
    private final Map<Field, Map<String, Field>> foreignLinkFieldsToColumnToFieldMap = new LinkedHashMap<>();

    public TableModelForQueries(Class<?> klass) {
        this.tableName = PersistenceAnnotationsUtils.extractTableName(klass);
        this.userId = userId;

        List<Field> allFields = ClassUtils.getFieldsUpToObject(klass);
        allFields.forEach(field -> {
            if (field.isAnnotationPresent(Column.class)) {

                field.setAccessible(true);

                String columnName = field.getAnnotation(Column.class).name();
                columns.add(columnName);
                columnToFieldMap.put(columnName, field);

                if (!Objects.equals(field.getAnnotation(Column.class).foreign(), Object.class)) {
                    Class<?> foreignClass = field.getAnnotation(Column.class).foreign();
                    addForeign(foreignClass, columnName);
                }
            }
        });
    }

    void addForeign(Class<?> klass, String foreignIdColumn) {
        String foreignTableName = PersistenceAnnotationsUtils.extractTableName(klass);
        List<Field> foreignFields = ClassUtils.getFieldsUpToObject(klass);

        columnToParentNameMap.put(foreignIdColumn, foreignTableName);

        List<String> foreignColumns = new ArrayList<>();

        foreignFields.forEach(field -> {
            if (field.isAnnotationPresent(Column.class)) {

                field.setAccessible(true);

                String foreignColumnName = field.getAnnotation(Column.class).name();
                foreignColumns.add(foreignColumnName);
            }
        });
        parentTablesColumnsMap.put(foreignTableName, foreignColumns);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public Map<String, String> getColumnToParentNameMap() {
        return columnToParentNameMap;
    }

    public Map<String, List<String>> getParentTablesColumnsMap() {
        return parentTablesColumnsMap;
    }

    public Map<String, Field> getColumnToFieldMap() {
        return columnToFieldMap;
    }

    public Map<Field, Map<String, Field>> getForeignLinkFieldsToColumnToFieldMap() {
        return foreignLinkFieldsToColumnToFieldMap;
    }

    @Override
    public String toString() {
        return "QueryModel{" +
                "userId='" + userId + '\'' +
                ", tableName='" + tableName + '\'' +
                ", columns=" + columns +
                ", parentTablesColumnsMap=" + parentTablesColumnsMap +
                ", columnToParentNameMap=" + columnToParentNameMap +
                '}';
    }
}