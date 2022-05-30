package kpersistence.v2.queryGeneration;

import kpersistence.v2.UnnamedParametersQuery;
import kpersistence.v2.modelsMaster.ModelsMaster;
import kpersistence.v2.modelsMaster.TableModelForQueries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertQueryGenerator {

    private final TableModelForQueries tableModel;
    private final Object model;
    private final String id;

    public InsertQueryGenerator(Object model, String userId, String id) {
        tableModel = ModelsMaster.getQueryModel(model.getClass());
        tableModel.setUserId(userId);
        this.model = model;
        this.id = id;
    }

    public UnnamedParametersQuery generateInsertQuery() {

        String userId = tableModel.getUserId();
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

        columns.append(", USER_ID)");
        values.append(", ?)");
        params.add(userId);

        sql.append(columns).append(values);

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
