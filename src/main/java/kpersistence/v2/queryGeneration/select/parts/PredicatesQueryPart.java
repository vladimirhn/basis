package kpersistence.v2.queryGeneration.select.parts;

import kpersistence.v2.annotations.PersistenceAnnotationsUtils;
import kpersistence.v2.modelsMaster.ModelsMaster;
import kpersistence.v2.modelsMaster.TableModelForQueries;
import kpersistence.v2.tables.StringIdTable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class PredicatesQueryPart {

    StringBuilder sql = new StringBuilder();

    public PredicatesQueryPart(StringIdTable mainTable, List<Object> params) {
        TableModelForQueries tableModel = ModelsMaster.getQueryModel(mainTable.getClass());
        String tableName = tableModel.getTableName();
        Map<String, Field> columnToFieldMap = tableModel.getColumnToFieldMap();
        Map<Field, Map<String, Field>> parentTablesToColumnToFieldMap = tableModel.getForeignLinkFieldsToColumnToFieldMap();

        columnToFieldMap.forEach((column, field) -> {
            try {
                Object datum = field.get(mainTable);

                if (datum != null) {
                    sql.append(" AND ").append(tableName).append(".").append(column).append(" = ?");
                    params.add(datum);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        parentTablesToColumnToFieldMap.forEach((foreignLinkField, foreignColumnsToFieldMap) -> {
            try {

                Object foreignObject = foreignLinkField.get(mainTable);
                if (foreignObject != null) {

                    String foreignTableName = PersistenceAnnotationsUtils.extractTableName(foreignObject.getClass());

                    for (String column : foreignColumnsToFieldMap.keySet()) {
                        Field field = foreignColumnsToFieldMap.get(column);

                        Object datum = field.get(foreignObject);

                        if (datum != null) {
                            sql.append(" AND ").append(foreignTableName).append(".").append(column).append(" = ?");
                            params.add(datum);
                        }
                    }

                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public String toString() {
        return sql.toString();
    }
}
