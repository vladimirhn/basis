package kpersistence.v2.queryGeneration;

import kpersistence.v2.UnnamedParametersQuery;
import kpersistence.v2.modelsMaster.ModelsMaster;
import kpersistence.v2.modelsMaster.queries.TableModelForAllDataQueries;

import java.util.ArrayList;
import java.util.List;

public class DeleteQueryGenerator {

    private final String id;
    private final String userId;
    private final String tableName;
    private final boolean isSoftDelete;

    public DeleteQueryGenerator(Class<?> model, String id, String userId) {
        if (id == null) {
            throw new IllegalArgumentException("Being deleted model must have an id. This one does not: " + model);
        }

        TableModelForAllDataQueries tableModel = ModelsMaster.getQueryAllDataModel(model);

        this.id = id;
        this.userId = userId;
        this.tableName = tableModel.getTableName();
        this.isSoftDelete = tableModel.isSoftDelete();

    }

    public UnnamedParametersQuery generateDeleteQuery() {

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (isSoftDelete) {
            sql.append("UPDATE ").append(tableName);
            sql.append(" SET deleted = true");

        } else {
            sql.append("DELETE FROM ").append(tableName);
        }

        sql.append(" WHERE USER_ID = ? AND ID = ?");
        params.add(userId);
        params.add(id);

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
