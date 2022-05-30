package kpersistence.v2.queryGeneration;

import kpersistence.v2.UnnamedParametersQuery;
import kpersistence.v2.modelsMaster.ModelsMaster;
import kpersistence.v2.modelsMaster.TableModelForQueries;
import kpersistence.v2.tables.StringIdTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectFilteredQueryGenerator {

    private final TableModelForQueries tableModel;
    private StringIdTable model;

    public SelectFilteredQueryGenerator(StringIdTable model, String userId) {
        tableModel = ModelsMaster.getQueryModel(model.getClass());
        tableModel.setUserId(userId);
        this.model = model;
    }

    public UnnamedParametersQuery generateSelectFilteredQuery() {

        String userId = tableModel.getUserId();
        String tableName = tableModel.getTableName();
        List<String> columns = tableModel.getColumns();
        Map<String, List<String>> parentTablesColumnsMap = tableModel.getParentTablesColumnsMap();
        Map<String, String> columnToParentNameMap = tableModel.getColumnToParentNameMap();

        Map<String, Field> columnToFieldMap = tableModel.getColumnToFieldMap();
        Map<Field, Map<String, Field>> parentTablesToColumnToFieldMap = tableModel.getForeignLinkFieldsToColumnToFieldMap();

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>(1);

        sql.append("SELECT ");

        String tableFieldsList = columns.stream()
                .map(col -> tableName + "." + col + " " + tableName + "__" + col)
                .collect(Collectors.joining(","));
        sql.append(tableFieldsList);

        if (!parentTablesColumnsMap.isEmpty()) {
            for (String parentTableName : parentTablesColumnsMap.keySet()) {
                sql.append(", ");

                String parentFieldsList = parentTablesColumnsMap.get(parentTableName).stream()
                        .map(col -> parentTableName + "." + col + " " + parentTableName + "__" + col)
                        .collect(Collectors.joining(","));
                sql.append(parentFieldsList);
            }
        }

        sql.append(" FROM ").append(tableName);

        if (!columnToParentNameMap.isEmpty()) {
            for (String referColumn : columnToParentNameMap.keySet()) {
                sql.append(" LEFT JOIN ").append(columnToParentNameMap.get(referColumn));
                String joinCondition = " ON " + tableName + "." + referColumn + " = " + columnToParentNameMap.get(referColumn) + ".ID";
                sql.append(joinCondition);
            }
        }

        if (userId != null) {
            sql.append(" WHERE " + tableName + ".USER_ID = ?");
            params.add(userId);
        } else {
            sql.append(" WHERE 1 = 1");
        }

        columnToFieldMap.forEach((column, field) -> {
            try {
                Object datum = field.get(model);

                if (datum != null) {
                    sql.append(" AND ").append(tableName).append(".").append(column).append(" = ?");
                    params.add(datum);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        parentTablesToColumnToFieldMap.forEach((foreignLinkField, foreignColumnsToFieldMap) -> {
            try {

                Object foreignObject = foreignLinkField.get(model);
                if (foreignObject != null) {

                    for (String column : foreignColumnsToFieldMap.keySet()) {
                        Field field = foreignColumnsToFieldMap.get(column);

                        Object datum = field.get(foreignObject);

                        if (datum != null) {
                            sql.append(" AND ").append(column).append(".").append(column).append(" = ?");
                            params.add(datum);
                        }
                    }

                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
