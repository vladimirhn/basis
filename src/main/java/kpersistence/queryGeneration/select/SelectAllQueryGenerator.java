package kpersistence.queryGeneration.select;

import kpersistence.UnnamedParametersQuery;
import kpersistence.modelsMaster.queries.TableModelForQueries;
import kpersistence.queryGeneration.parts.OrderByQueryPart;
import kpersistence.queryGeneration.parts.SelectFromQueryPart;
import kpersistence.queryGeneration.parts.WhereQueryPart;

import java.util.ArrayList;
import java.util.List;

public class SelectAllQueryGenerator {

    private final String userId;
    private final String orderByFieldName;
    private final String direction;

    private final TableModelForQueries tableModel;

    private final StringBuilder sql = new StringBuilder();
    private final List<Object> params = new ArrayList<>(1);
    private final boolean isSoftDelete;

    public SelectAllQueryGenerator(TableModelForQueries tableModel, String userId, String orderByFieldName, String direction) {
        this.tableModel = tableModel;
        this.userId = userId;
        this.orderByFieldName = orderByFieldName;
        this.direction = direction;
        this.isSoftDelete = tableModel.isSoftDelete();
    }

    public UnnamedParametersQuery generateSelectAllQuery() {

        sql.append(new SelectFromQueryPart(tableModel));
        sql.append(new WhereQueryPart(tableModel, userId, params, isSoftDelete));
        sql.append(orderByFieldName != null ?
                new OrderByQueryPart(tableModel, orderByFieldName, direction) :
                new OrderByQueryPart(tableModel));

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
