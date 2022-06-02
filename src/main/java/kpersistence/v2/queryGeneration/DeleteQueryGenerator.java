package kpersistence.v2.queryGeneration;

import kpersistence.v2.UnnamedParametersQuery;
import kpersistence.v2.modelsMaster.ModelsMaster;

import java.util.ArrayList;
import java.util.List;

public class DeleteQueryGenerator {

    String id;
    String userId;
    String tableName;

    public DeleteQueryGenerator(Class<?> model, String id, String userId) {
        if (id == null) {
            throw new IllegalArgumentException("Being deleted model must have an id. This one does not: " + model);
        }

        this.id = id;
        this.userId = userId;
        this.tableName = ModelsMaster.getQueryAllDataModel(model).getTableName();
    }

    public UnnamedParametersQuery generateDeleteQuery() {

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("DELETE FROM ").append(tableName);
        sql.append(" WHERE USER_ID = ? AND ID = ?");
        params.add(userId);
        params.add(id);

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
