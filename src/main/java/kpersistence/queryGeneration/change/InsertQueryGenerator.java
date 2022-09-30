package kpersistence.queryGeneration.change;

import kpersistence.UnnamedParametersQuery;
import kpersistence.modelsMaster.ModelsMaster;
import kpersistence.modelsMaster.queries.TableModelForAllDataQueries;
import kpersistence.tables.UserIdTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertQueryGenerator {

    private final String userId;

    private final TableModelForAllDataQueries tableModel;
    private final Object model;
    private final String id;

    public InsertQueryGenerator(Object model, String userId, String id) {
        tableModel = ModelsMaster.getQueryAllDataModel(model.getClass());
        this.userId = userId;
        this.model = model;
        this.id = id;
    }

    public UnnamedParametersQuery generateInsertQuery() {

        String tableName = tableModel.getTableName();

        Map<String, Field> columnToFieldMap = tableModel.getColumnToFieldMap();

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("INSERT INTO ").append(tableName);

        StringBuilder columns = new StringBuilder(" (ID");
        StringBuilder values = new StringBuilder(" VALUES (?");
        params.add(id);

        columnToFieldMap.forEach((column, field) -> {
            try {
                Object datum = field.get(model);
                if (datum != null) {
                    columns.append(", ").append(column);
                    values.append(", ?");
                    params.add(datum);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        if (model instanceof UserIdTable) {
            columns.append(", USER_ID)");
            values.append(", ?)");
            params.add(userId);
        } else {
            columns.append(")");
            values.append(")");
        }

        sql.append(columns).append(values);

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
