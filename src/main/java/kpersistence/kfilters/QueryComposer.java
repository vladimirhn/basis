package kpersistence.kfilters;

import kpersistence.mapping.annotations.Column;
import kpersistence.mapping.annotations.Table;
import kutils.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;

public class QueryComposer<T> {

    private Class<T> clazz;

    private Map<String, Object> params = new HashMap<>();
    private List<Boolean> isOrFlags = new ArrayList<>();
    private List<Object> labels = new ArrayList<>();
    private List<SqlOperator> operators = new ArrayList<>();
    private RamMap labelToColumn = new RamMap();

    private String tableName = "";
    private String predicates = "";

    public QueryComposer(Class<T> clazz, Filter<T, ?>[] initFilters) {
        try {
            if (initFilters == null || initFilters.length == 0) {
                throw new IllegalArgumentException("Фильтры не обнаружены. Для работы метода нужен хотя бы один Filter.");
            }

            this.clazz = clazz;
            List<T> instances = new LinkedList<>();

            Table table = clazz.getAnnotation(Table.class);
            tableName = table.name();

            int i = 0;
            for (Filter<T, ?> filter : initFilters) {
                T instance = clazz.getDeclaredConstructor().newInstance();
                if (filter == null || filter.getValues() == null) {
                    continue;
                }
                this.params.put("param" + i++, filter.getValues());
                this.isOrFlags.add(filter.isOrFilter());
                this.labels.add(filter.setUpLabel(instance));
                this.operators.add(filter.getSqlOperator());

                instances.add(instance);
            }

            List<Field> fields = ClassUtils.getFieldsUpToObject(clazz);

            INSTANCE:
            for (T instance : instances) {

                for (Field field : fields) {
                    field.setAccessible(true);

                    if (field.get(instance) != null) {

                        Column column = field.getAnnotation(Column.class);
                        if (column != null) {
                            labelToColumn.put(field.get(instance), column.name());
                            continue INSTANCE;
                        }
                    }
                }
            }

            composePredicates();

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            System.out.println(String.format("Не удалось создать экземпляр класса %s для продвинутого поиска.", clazz.getName()));
            e.printStackTrace();
        }

    }

    private void composePredicates() {

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new UnsupportedOperationException(String.format("У модели %s отсутствует аннотация @Table", clazz.getSimpleName()));
        }

        String firstPredicate = " WHERE " + labelToColumn.get(labels.get(0)) + String.format(operators.get(0).getUsage(), ":param0");

        String restPredicates = "";
        for (int i = 1; i < labels.size(); i++) {
            String conjuction = isOrFlags.get(i) ? "   OR " : "  AND ";
            restPredicates += "\n " + conjuction + labelToColumn.get(labels.get(i)) + String.format(operators.get(i).getUsage(), ":param" + i);
        }

        predicates = firstPredicate + restPredicates;
    }

    public String getSelectSql() {
        return "\nSELECT * FROM " + tableName +
                "\n" + predicates;
    }

    private class DatabaseVersion {
        public boolean isMsSql() {return false;}
        public boolean isOracle() {return false;}
        public boolean isOracle12plus() {return false;}
        public boolean isPostgreSql() {return true;}
    }

    public String getSelectSql(int limit) {

        DatabaseVersion dbData = new DatabaseVersion();

        if (dbData.isMsSql()) {
            return "\nSELECT TOP " + limit + " * FROM " + tableName +
                    "\n" + predicates;
        }

        if (dbData.isOracle()) {
            return "\nSELECT * FROM " + tableName +
                    "\n" + predicates +
                    "\n AND ROWNUM <= " + limit;

        }

        if (dbData.isPostgreSql()) {
            return "\nSELECT * FROM " + tableName +
                    "\n" + predicates +
                    "\n LIMIT " + limit;
        }

        throw new IllegalStateException("Unknown database type!");
    }

    public String getSelectSql(int limit, int offset) {

        DatabaseVersion dbData = new DatabaseVersion();

        if (dbData.isMsSql()) {
            return "\nSELECT * FROM " + tableName +
                    "\n" + predicates +
                    "\n ORDER BY (SELECT NULL)" +
                    "\n OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
        }

        if (dbData.isOracle12plus()) {
            return "\nSELECT * FROM " + tableName +
                    "\n" + predicates +
                    "\n ORDER BY NULL" +
                    "\n OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";

        }

        if (dbData.isOracle()) {
            return "\nWITH NUM_ROW AS (SELECT ROW_NUMBER() OVER (ORDER BY NULL DESC) AS ROWRANK, " + tableName + ".*"
                    + "\n FROM " + tableName
                    + "\n" + predicates + ")"
                    + "\nSELECT * FROM NUM_ROW"
                    + "\n WHERE ROWRANK BETWEEN " + (offset + 1) + " AND " + (limit + offset);
        }

        if (dbData.isPostgreSql()) {
            return "\nSELECT * FROM " + tableName +
                    "\n" + predicates +
                    "\n OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
        }

        throw new IllegalStateException("Unknown database type!");
    }

    public <O> String getSelectSql(int limit, int offset, Class<O> orderByFieldType, BiConsumer<T, O> orderByFieldSetter) {

        DatabaseVersion dbData = new DatabaseVersion();

        String orderByColumnName = new SetterToColumnMapper<T>().map(this.clazz, orderByFieldSetter, orderByFieldType);

        if (dbData.isMsSql() || dbData.isOracle12plus() || dbData.isPostgreSql()) {
            return "\nSELECT * FROM " + tableName +
                    "\n" + predicates +
                    "\n ORDER BY " + orderByColumnName +
                    "\n OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
        }

        if (dbData.isOracle()) {
            return "\nWITH NUM_ROW AS (SELECT ROW_NUMBER() OVER (ORDER BY " + orderByColumnName + " DESC) AS ROWRANK, " + tableName + ".*"
                    + "\n FROM " + tableName
                    + "\n" + predicates + ")"
                    + "\nSELECT * FROM NUM_ROW"
                    + "\n WHERE ROWRANK BETWEEN " + (offset + 1) + " AND " + (limit + offset);
        }

        throw new IllegalStateException("Unknown database type!");
    }

    public String getCountSql() {
        return "\nSELECT COUNT(*) FROM " + tableName +
                "\n" + predicates;
    }

    public <S> String getSumSql(Class<S> sumFieldType, BiConsumer<T, S> sumFieldSetter) {

        String sumColumnName = new SetterToColumnMapper<T>().map(this.clazz, sumFieldSetter, sumFieldType);

        return "\nSELECT SUM(" + sumColumnName + ") FROM " + tableName +
                "\n" + predicates;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public <V> String getSelectDistinctSql(Class<V> fieldType, BiConsumer<T, V> fieldSetter) {
        String columnName = new SetterToColumnMapper<T>().map(this.clazz, fieldSetter, fieldType);
        return String.format("SELECT DISTINCT %s FROM %s", columnName, tableName) + " " + predicates;
    }
}
