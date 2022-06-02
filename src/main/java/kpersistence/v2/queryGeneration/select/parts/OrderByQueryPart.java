package kpersistence.v2.queryGeneration.select.parts;

import kpersistence.v2.annotations.Column;
import kpersistence.v2.modelsMaster.TableModelForQueries;

import java.lang.reflect.Field;
import java.util.Map;

public class OrderByQueryPart {

    StringBuilder sql = new StringBuilder();

    public OrderByQueryPart(TableModelForQueries tableModel, String fieldName, String direction) {
        Map<String, Field> fieldNameToFieldMap = tableModel.getFieldNameToFieldMap();

        if (fieldName != null) {
            String tableName = tableModel.getTableName();
            String columnNane = fieldNameToFieldMap.get(fieldName).getAnnotation(Column.class).name();
            sql.append(" ORDER BY ").append(tableName).append(".").append(columnNane).append(" ").append(direction.toUpperCase());
        }
    }

    @Override
    public String toString() {
        return sql.toString();
    }
}
