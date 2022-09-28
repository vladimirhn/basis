package kpersistence.queryGeneration.parts;

import kpersistence.modelsMaster.queries.TableModelForQueries;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectFromQueryPart {

    StringBuilder sql = new StringBuilder();

    private final String tableName;
    private final List<String> columns;
    private final Map<String, List<String>> parentTablesColumnsMap;
    private final Map<String, String> columnToParentNameMap;

    public SelectFromQueryPart(TableModelForQueries tableModel) {
        tableName = tableModel.getTableName();
        columns = tableModel.getColumns();
        parentTablesColumnsMap = tableModel.getForeignTablesColumnsMap();
        columnToParentNameMap = tableModel.getColumnToForeignTableNameMap();

        composeSelectClause();
        composeFromClause();
    }

    @Override
    public String toString() {
        return sql.toString();
    }

    private void composeSelectClause() {
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
    }

    private void composeFromClause() {
        sql.append(" FROM ").append(tableName);

        if (!columnToParentNameMap.isEmpty()) {
            for (String referColumn : columnToParentNameMap.keySet()) {
                sql.append(" LEFT JOIN ").append(columnToParentNameMap.get(referColumn));
                String joinCondition = " ON " + tableName + "." + referColumn + " = " + columnToParentNameMap.get(referColumn) + ".ID";
                sql.append(joinCondition);
            }
        }
    }
}
