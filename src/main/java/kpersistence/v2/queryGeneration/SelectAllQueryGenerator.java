package kpersistence.v2.queryGeneration;

import kpersistence.v2.UnnamedParametersQuery;
import kpersistence.v2.modelsMaster.ModelsMaster;
import kpersistence.v2.modelsMaster.TableModelForQueries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectAllQueryGenerator {

    private final TableModelForQueries tableModel;

    public SelectAllQueryGenerator(Class<?> klass, String userId) {
        tableModel = ModelsMaster.getQueryModel(klass);
        tableModel.setUserId(userId);
    }

    public UnnamedParametersQuery generateSelectAllQuery() {

        String userId = tableModel.getUserId();
        String tableName = tableModel.getTableName();
        List<String> columns = tableModel.getColumns();
        Map<String, List<String>> parentTablesColumnsMap = tableModel.getParentTablesColumnsMap();
        Map<String, String> columnToParentNameMap = tableModel.getColumnToParentNameMap();

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
        }

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
