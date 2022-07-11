package kpersistence.v2.queryGeneration.select;

import kpersistence.v2.UnnamedParametersQuery;
import kpersistence.v2.modelsMaster.queries.TableModelForQueries;
import kpersistence.v2.queryGeneration.select.parts.PredicatesQueryPart;
import kpersistence.v2.queryGeneration.select.parts.SelectFromQueryPart;
import kpersistence.v2.queryGeneration.select.parts.WhereUserIdQueryPart;
import kpersistence.v2.tables.Table;

import java.util.ArrayList;
import java.util.List;

public class SelectFilteredQueryGenerator {

    private final String userId;

    StringBuilder sql = new StringBuilder();

    private final TableModelForQueries tableModel;
    private Table mainTable;

    public SelectFilteredQueryGenerator(TableModelForQueries tableModel, Table mainTable, String userId) {
        this.tableModel = tableModel;
        this.userId = userId;
        this.mainTable = mainTable;
    }

    public UnnamedParametersQuery generateSelectFilteredQuery() {

        List<Object> params = new ArrayList<>();

        sql.append(new SelectFromQueryPart(tableModel));
        sql.append(new WhereUserIdQueryPart(tableModel, userId, params));
        sql.append(new PredicatesQueryPart(mainTable, params));

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
