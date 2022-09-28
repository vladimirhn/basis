package kpersistence.queryGeneration.parts;

import kpersistence.modelsMaster.queries.TableModelForQueries;

import java.util.List;

public class WhereQueryPart {

    StringBuilder sql = new StringBuilder();

    public WhereQueryPart(TableModelForQueries tableModel, String userId, List<Object> params, boolean isSoftDelete) {

        if (userId != null) {
            sql.append(" WHERE ").append(tableModel.getTableName()).append(".USER_ID = ?");
            params.add(userId);
        } else {
            sql.append(" WHERE 1 = 1");
        }

        if (isSoftDelete) {
            sql.append(" AND ").append(tableModel.getTableName()).append(".DELETED != true");
        }
    }

    @Override
    public String toString() {
        return sql.toString();
    }
}
