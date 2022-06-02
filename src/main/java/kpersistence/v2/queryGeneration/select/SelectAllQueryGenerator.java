package kpersistence.v2.queryGeneration.select;

import kpersistence.v2.UnnamedParametersQuery;
import kpersistence.v2.modelsMaster.ModelsMaster;
import kpersistence.v2.modelsMaster.queries.TableModelForAllDataQueries;
import kpersistence.v2.modelsMaster.queries.TableModelForQueries;
import kpersistence.v2.queryGeneration.select.parts.OrderByQueryPart;
import kpersistence.v2.queryGeneration.select.parts.SelectFromQueryPart;
import kpersistence.v2.queryGeneration.select.parts.WhereUserIdQueryPart;

import java.util.ArrayList;
import java.util.List;

public class SelectAllQueryGenerator {

    private final String userId;
    private final String orderByFieldName;
    private final String direction;

    private final TableModelForQueries tableModel;

    private final StringBuilder sql = new StringBuilder();
    private final List<Object> params = new ArrayList<>(1);

    public SelectAllQueryGenerator(TableModelForQueries tableModel, String userId, String orderByFieldName, String direction) {
        this.tableModel = tableModel;
        this.userId = userId;
        this.orderByFieldName = orderByFieldName;
        this.direction = direction;
    }

    public UnnamedParametersQuery generateSelectAllQuery() {

        sql.append(new SelectFromQueryPart(tableModel));
        sql.append(new WhereUserIdQueryPart(tableModel, userId, params));
        sql.append(new OrderByQueryPart(tableModel, orderByFieldName, direction));

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
