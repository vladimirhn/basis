package kpersistence.queryGeneration.change;

import kpersistence.UnnamedParametersQuery;
import kpersistence.modelsMaster.ModelsMaster;
import kpersistence.modelsMaster.queries.TableModelForAllDataQueries;
import kpersistence.queryGeneration.parts.PredicatesQueryPart;
import kpersistence.tables.Table;

import java.util.ArrayList;
import java.util.List;

public class DeleteSimilarQueryGenerator<T extends Table> {

    private final T data;
    private final String userId;
    private final String tableName;
    private final boolean isSoftDelete;

    public DeleteSimilarQueryGenerator(Class<T> model, T data, String userId) {

        TableModelForAllDataQueries tableModel = ModelsMaster.getQueryAllDataModel(model);

        this.data = data;
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

        sql.append(" WHERE USER_ID = ? ");
        params.add(userId);
        sql.append(new PredicatesQueryPart(data, params));

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
