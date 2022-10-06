package kpersistence.queryGeneration.change;

import kpersistence.UnnamedParametersQuery;
import kpersistence.annotations.InsertDefault;
import kpersistence.modelsMaster.ModelsMaster;
import kpersistence.modelsMaster.queries.TableModelForAllDataQueries;
import kpersistence.tables.UserIdTable;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertQueryGenerator {

    private final String userId;

    private final TableModelForAllDataQueries tableModel;
    private final Object model;
    private final String id;

    public InsertQueryGenerator(Object model, String userId, String id) {
        tableModel = ModelsMaster.getQueryAllDataModel(model.getClass());
        this.userId = userId;
        this.model = model;
        this.id = id;

        trySetDefaults();
    }

    private void trySetDefaults() {
        try {
            setDefaults();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefaults() throws Exception {
        for (Field field : model.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(InsertDefault.class)) {
                field.setAccessible(true);

                if (field.getType() == String.class) {
                    field.set(model, field.getAnnotation(InsertDefault.class).s());
                }
                else if (field.getType() == Boolean.class) {
                    field.set(model, field.getAnnotation(InsertDefault.class).b());
                }
                else if (field.getType() == Integer.class) {
                    field.set(model, field.getAnnotation(InsertDefault.class).i());
                }
                else if (field.getType() == Long.class) {
                    field.set(model, field.getAnnotation(InsertDefault.class).l());
                }
                else if (field.getType() == Double.class) {
                    field.set(model, field.getAnnotation(InsertDefault.class).d());
                }
                else if (field.getType() == LocalDate.class) {

                    String defaultValue = field.getAnnotation(InsertDefault.class).date();
                    LocalDate value;
                    if ("now".equals(defaultValue)) {
                        value = LocalDate.now();
                    } else {
                        value = LocalDate.parse(defaultValue);
                    }

                    field.set(model, value);
                }
                else if (field.getType() == LocalDateTime.class) {

                    String defaultValue = field.getAnnotation(InsertDefault.class).dateTime();
                    LocalDateTime value;
                    if ("now".equals(defaultValue)) {
                        value = LocalDateTime.now();
                    } else {
                        value = LocalDateTime.parse(defaultValue);
                    }

                    field.set(model, value);
                }
            }
        }
    }

    public UnnamedParametersQuery generateInsertQuery() {

        String tableName = tableModel.getTableName();

        Map<String, Field> columnToFieldMap = tableModel.getColumnToFieldMap();

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("INSERT INTO ").append(tableName);

        StringBuilder columns = new StringBuilder(" (ID");
        StringBuilder values = new StringBuilder(" VALUES (?");
        params.add(id);

        columnToFieldMap.forEach((column, field) -> {
            try {
                Object datum = field.get(model);
                if (datum != null) {
                    columns.append(", ").append(column);
                    values.append(", ?");
                    params.add(datum);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        if (model instanceof UserIdTable) {
            columns.append(", USER_ID)");
            values.append(", ?)");
            params.add(userId);
        } else {
            columns.append(")");
            values.append(")");
        }

        sql.append(columns).append(values);

        return new UnnamedParametersQuery(sql.toString(), params);
    }
}
