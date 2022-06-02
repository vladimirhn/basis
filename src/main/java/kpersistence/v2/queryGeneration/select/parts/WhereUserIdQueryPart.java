package kpersistence.v2.queryGeneration.select.parts;

import kpersistence.v2.modelsMaster.queries.TableModelForQueries;

import java.util.List;

public class WhereUserIdQueryPart {

    StringBuilder sql = new StringBuilder();

    public WhereUserIdQueryPart(TableModelForQueries tableModel, String userId, List<Object> params) {

        if (userId != null) {
            sql.append(" WHERE ").append(tableModel.getTableName()).append(".USER_ID = ?");
            params.add(userId);
        } else {
            sql.append(" WHERE 1 = 1");
        }
    }

    @Override
    public String toString() {
        return sql.toString();
    }
}
