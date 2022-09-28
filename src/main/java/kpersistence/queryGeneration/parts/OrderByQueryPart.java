package kpersistence.queryGeneration.parts;

import kpersistence.annotations.OrderBy;
import kpersistence.annotations.Column;
import kpersistence.modelsMaster.queries.TableModelForQueries;

import java.lang.reflect.Field;
import java.util.*;

public class OrderByQueryPart {

    StringBuilder sql = new StringBuilder();

    public OrderByQueryPart(TableModelForQueries tableModel, String fieldName, String direction) {

        Map<String, Field> fieldNameToFieldMap = tableModel.getFieldNameToFieldMap();

        if (fieldName != null) {
            direction = direction != null ? direction : "ASC";
            String tableName = tableModel.getTableName();
            String columnNane = fieldNameToFieldMap.get(fieldName).getAnnotation(Column.class).name();
            sql.append(" ORDER BY ").append(tableName).append(".").append(columnNane).append(" ").append(direction.toUpperCase());
        }
    }

    public OrderByQueryPart(TableModelForQueries tableModel) {

        List<Field> orderByFields = tableModel.getOrderByFields();
        Map<String, Field> fieldNameToFieldMap = tableModel.getFieldNameToFieldMap();

        if (!orderByFields.isEmpty()) {

            orderByFields.sort(Comparator.comparingInt(o -> o.getAnnotation(OrderBy.class).priority()));

            List<String> tableColumnPairs = new ArrayList<>();
            String tableName = tableModel.getTableName();
            sql.append(" ORDER BY ");

            orderByFields.forEach(field -> {
                StringBuilder tableColumnPair = new StringBuilder(64);
                String columnName = fieldNameToFieldMap.get(field.getName()).getAnnotation(Column.class).name();
                String direction = field.getAnnotation(OrderBy.class).direction().name();

                tableColumnPair.append(tableName).append(".").append(columnName).append(" ").append(direction);
                tableColumnPairs.add(tableColumnPair.toString());
            });

            sql.append(String.join(", ", tableColumnPairs));
        }
    }

    @Override
    public String toString() {
        return sql.toString();
    }
}
