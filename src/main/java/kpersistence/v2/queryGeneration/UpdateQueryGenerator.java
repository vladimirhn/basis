package kpersistence.v2.queryGeneration;

import kpersistence.v2.UnnamedParametersQuery;
import kpersistence.v2.modelsMaster.ModelsMaster;
import kpersistence.v2.modelsMaster.TableModelForQueries;
import kpersistence.v2.tables.StringIdTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateQueryGenerator {

    private final TableModelForQueries tableModel;
    private final StringIdTable model;

    public UpdateQueryGenerator(StringIdTable model, String userId) {
        tableModel = ModelsMaster.getQueryModel(model.getClass());
        tableModel.setUserId(userId);
        this.model = model;

        if (model.getId() == null) {
            throw new IllegalArgumentException("Being updated model must have an id. This one does not: " + model);
        }
    }

    public UnnamedParametersQuery generateInsertQuery() {

        String userId = tableModel.getUserId();
        String tableName = tableModel.getTableName();

        Map<String, Field> columnToFieldMap = tableModel.getColumnToFieldMap();

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("UPDATE ").append(tableName).append(" SET ");
        List<String> columnsEquals = new ArrayList<>(); // "COL_NAME = ?"

        columnToFieldMap.forEach((column, field) -> {
            try {
                if (!Objects.equals(column, "ID")) {
                    Object datum = field.get(model);

                    columnsEquals.add(column + " = ?");
                    params.add(datum);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        sql.append(String.join(", ", columnsEquals));

        sql.append(" WHERE USER_ID = ? AND ID = ?");
        params.add(userId);
        params.add(model.getId());


        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
