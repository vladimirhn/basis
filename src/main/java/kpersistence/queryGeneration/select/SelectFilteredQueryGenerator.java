package kpersistence.queryGeneration.select;

import kpersistence.UnnamedParametersQuery;
import kpersistence.modelsMaster.queries.TableModelForQueries;
import kpersistence.queryGeneration.parts.PredicatesQueryPart;
import kpersistence.queryGeneration.parts.SelectFromQueryPart;
import kpersistence.queryGeneration.parts.WhereQueryPart;
import kpersistence.tables.Table;

import java.util.ArrayList;
import java.util.List;

public class SelectFilteredQueryGenerator {

    private final String userId;

    StringBuilder sql = new StringBuilder();

    private final TableModelForQueries tableModel;
    private final Table mainTable;
    private final boolean isSoftDelete;

    public SelectFilteredQueryGenerator(TableModelForQueries tableModel, Table mainTable, String userId) {
        this.tableModel = tableModel;
        this.userId = userId;
        this.mainTable = mainTable;
        this.isSoftDelete = tableModel.isSoftDelete();
    }

    public UnnamedParametersQuery generateSelectFilteredQuery() {

        List<Object> params = new ArrayList<>();

        sql.append(new SelectFromQueryPart(tableModel));
        sql.append(new WhereQueryPart(tableModel, userId, params, isSoftDelete));
        sql.append(new PredicatesQueryPart(mainTable, params));

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
