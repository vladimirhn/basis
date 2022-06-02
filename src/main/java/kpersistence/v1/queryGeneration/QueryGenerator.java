package kpersistence.v1.queryGeneration;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import kcollections.CollectionFactory;
import kcollections.KList;
import kpersistence.v2.CurrentUserIdProvider;
import kpersistence.v2.UnnamedParametersQuery;
import kpersistence.v1.domain.Tables;
import kpersistence.v1.exceptions.AnnotationException;
import kpersistence.v1.exceptions.TableAnnotationException;
import kpersistence.v1.kfilters.SqlOperator;
import kpersistence.v1.mapping.annotations.*;
import kpersistence.v1.query.QueryProperties;
import kpersistence.v1.query.SqlPredicate;
import kpersistence.v2.annotations.Column;
import kpersistence.v2.annotations.Foreign2;
import kpersistence.v2.annotations.Id;
import kpersistence.v2.annotations.Table;
import kpersistence.v2.queryGeneration.select.SelectAllQueryGenerator;
import kutils.ClassUtils;

public class QueryGenerator {

    public static CurrentUserIdProvider currentUserIdProvider;

    public static UnnamedParametersQuery generateSelectOneQuery(String id, Class<?> type) throws AnnotationException {
        String tableName = extractTableName(type);
        String idColumn = extractIdColumnName(type);

        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";

        return new UnnamedParametersQuery(sql, Collections.singletonList(id));
    }

    public static <T> UnnamedParametersQuery generateSelectQuery(QueryProperties<T> props) {

        Class<T> type = props.getClazz();
        String sql = generateSelectAllQuery(type);
        List<Object> params = new LinkedList<>();

        for (SqlPredicate predicate : props.getFilters()) {
            sql += " AND " + predicate.getColumn() + String.format(predicate.getOperator().getUsage(), "?");
            params.add(predicate.getValue());
        }

//        List<Field> orders = ClassUtils.getFieldsByAnnotation(type, OrderBy.class);
//
//        if (props.getOrderBy() != null) {
//
//            sql += " ORDER BY " + props.getOrderBy() + " " + props.getOrderDirection().name();
//
//        } else  if (orders.size() == 1) {
//
//            if (orders.get(0).isAnnotationPresent(Column.class)) {
//                sql += " ORDER BY "
//                        + orders.get(0).getAnnotation(Column.class).name()
//                        + " "
//                        + orders.get(0).getAnnotation(OrderBy.class).direction().name();
//
//            } else if (orders.get(0).isAnnotationPresent(Foreign.class)) {
//
//                Class<?> foreignTableClass = orders.get(0).getAnnotation(Foreign.class).table();
//                String foreignTableName = extractTableName(foreignTableClass);
//                String foreignColumnName = ClassUtils
//                        .getFieldsByAnnotation(foreignTableClass, Label.class).get(0)
//                        .getAnnotation(Column.class).name();
//
//                sql += " ORDER BY " + foreignTableName + "." + foreignColumnName + " " + orders.get(0).getAnnotation(OrderBy.class).direction().name();
//
//            }
//        }

        return new UnnamedParametersQuery(sql, params);
    }

    public static <T> String generateSelectAllQuery(Class<T> type) throws AnnotationException {

        String sql;
        String tableName = extractTableName(type);

        List<Field> currentUserId = ClassUtils.getFieldsByAnnotation(type, CurrentUserId.class);
        String userIdColumnName = currentUserId.get(0).getAnnotation(CurrentUserId.class).columnName();

        if (ClassUtils.getFieldsByAnnotation(type, Foreign.class).isEmpty()) {
            sql = "SELECT * FROM " + tableName;
        } else {
            sql = generateSelectAllQueryWithForeigns(type, tableName);
            userIdColumnName = tableName+"."+userIdColumnName;
        }

        sql += " WHERE 1 = 1 ";

        if (currentUserId.size() == 1) {
            if (currentUserIdProvider != null && currentUserIdProvider.getCurrentUserId() != null) {
                sql += " AND " + userIdColumnName + " = '" + currentUserIdProvider.getCurrentUserId() + "'";

            }
        }

        List<Field> orders = ClassUtils.getFieldsByAnnotation(type, OrderBy.class);
        if (orders.size() == 1) {

            if (orders.get(0).isAnnotationPresent(Column.class)) {
                sql += " ORDER BY "
                        + orders.get(0).getAnnotation(Column.class).name()
                        + " "
                        + orders.get(0).getAnnotation(OrderBy.class).direction().name();

            } else if (orders.get(0).isAnnotationPresent(Foreign.class)) {

                Class<?> foreignTableClass = orders.get(0).getAnnotation(Foreign.class).table();
                String foreignTableName = extractTableName(foreignTableClass);
                String foreignColumnName = ClassUtils
                        .getFieldsByAnnotation(foreignTableClass, Label.class).get(0)
                        .getAnnotation(Column.class).name();

                sql += " ORDER BY " + foreignTableName + "." + foreignColumnName + " " + orders.get(0).getAnnotation(OrderBy.class).direction().name();

            }
        }

        return sql;
    }

    public static <T> UnnamedParametersQuery generateSelectFilteredQuery(Class<T> type, Map<String, String> filters) throws AnnotationException {

        String sql;
        String tableName = extractTableName(type);

        List<Field> currentUserId = ClassUtils.getFieldsByAnnotation(type, CurrentUserId.class);
        String userIdColumnName = currentUserId.get(0).getAnnotation(CurrentUserId.class).columnName();

        if (ClassUtils.getFieldsByAnnotation(type, Foreign.class).isEmpty()) {
            sql = "SELECT * FROM " + tableName;
        } else {
            sql = generateSelectAllQueryWithForeigns(type, tableName);
            userIdColumnName = tableName + "." + userIdColumnName;
        }

        sql += " WHERE 1 = 1 ";

        if (currentUserId.size() == 1) {
            if (currentUserIdProvider != null && currentUserIdProvider.getCurrentUserId() != null) {
                sql += " AND " + userIdColumnName + " = '" + currentUserIdProvider.getCurrentUserId() + "'";

            }
        }

        KList<SqlPredicate> predicates = CollectionFactory.makeLinkedList();
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            String foreignId = filter.getValue();
            StringBuilder foreignIdColumn = new StringBuilder();

            ClassUtils.getFieldsByAnnotation(type, Destination.class).stream()
                    .filter(field -> {
                        Class<?> filterClass = Tables.getModelClassByName(filter.getKey());
                        Class<?> foreignClass = field.getAnnotation(Destination.class).value();
                        return Objects.equals(filterClass, foreignClass);
                    })
                    .findFirst().ifPresent(field -> {
                foreignIdColumn.append(field.getAnnotation(Column.class).name());
            });

            if (foreignIdColumn.length() > 0) {
                predicates.add(new SqlPredicate(foreignIdColumn.toString(), SqlOperator.EQUALS, foreignId));
            }
        }

        List<Object> values = new ArrayList<>(predicates.size());

        for (SqlPredicate predicate : predicates) {
            sql += " AND " + predicate.getColumn() + predicate.getOperator().getUnnamedUsage();
            values.add(predicate.getValue());
        }

        List<Field> orders = ClassUtils.getFieldsByAnnotation(type, OrderBy.class);
        if (orders.size() == 1) {

            if (orders.get(0).isAnnotationPresent(Column.class)) {
                sql += " ORDER BY "
                        + orders.get(0).getAnnotation(Column.class).name()
                        + " "
                        + orders.get(0).getAnnotation(OrderBy.class).direction().name();

            } else if (orders.get(0).isAnnotationPresent(Foreign.class)) {

                Class<?> foreignTableClass = orders.get(0).getAnnotation(Foreign.class).table();
                String foreignTableName = extractTableName(foreignTableClass);
                String foreignColumnName = ClassUtils
                        .getFieldsByAnnotation(foreignTableClass, Label.class).get(0)
                        .getAnnotation(Column.class).name();

                sql += " ORDER BY " + foreignTableName + "." + foreignColumnName + " " + orders.get(0).getAnnotation(OrderBy.class).direction().name();

            }
        }

        return new UnnamedParametersQuery(sql, values);
    }

    public static <T> String generateSelectIdToLabelsQuery(Class<T> type) {

        String sql;
        String tableName = extractTableName(type);

        List<Field> currentUserId = ClassUtils.getFieldsByAnnotation(type, CurrentUserId.class);
        String userIdColumnName = currentUserId.get(0).getAnnotation(CurrentUserId.class).columnName();

        String labelColumnName = ClassUtils
                .getFieldsByAnnotation(type, Label.class).get(0)
                .getAnnotation(Column.class).name();

        sql = "SELECT id, " + labelColumnName + " FROM " + tableName;

        sql += " WHERE 1 = 1 ";

        if (currentUserId.size() == 1) {
            if (currentUserIdProvider != null && currentUserIdProvider.getCurrentUserId() != null) {
                sql += " AND " + userIdColumnName + " = '" + currentUserIdProvider.getCurrentUserId() + "'";

            }
        }

        return sql;
    }

    public static <T> String generateSelectIdToLabelsWithParentQuery(Class<T> type, Class<?> filterClass) {

        String sql;
        String tableName = extractTableName(type);
        String labelColumnName = ClassUtils
                .getFirstFieldByAnnotation(type, Label.class)
                .getAnnotation(Column.class).name();
        String linkColumnName = ClassUtils
                .getFirstFieldByAnnotation(type, ParentId.class)
                .getAnnotation(Column.class).name();


        Field parent = ClassUtils.getFirstFieldByAnnotation(type, ParentId.class);
        Class<?> parentTableClass = parent.getAnnotation(ParentId.class).table();
        String parentTableName = extractTableName(parentTableClass);
        String parentLabelColumnName = ClassUtils
                .getFirstFieldByAnnotation(parentTableClass, Label.class)
                .getAnnotation(Column.class).name();

        String select = "SELECT DISTINCT "
                + parentTableName + ".ID, " + parentTableName + "." + parentLabelColumnName
                + ", " + tableName + ".ID, " + tableName + "." + labelColumnName;

        String from = " FROM " + tableName + " LEFT JOIN " + parentTableName +
                      " ON " + tableName + "." + linkColumnName + " = " + parentTableName + ".ID ";


        sql = select + from;

        if (filterClass != null) {

            StringBuilder rightJoin = new StringBuilder();

            ClassUtils
                    .getFieldsByAnnotation(filterClass, Destination.class)
                    .stream().filter(field -> type.equals(field.getAnnotation(Destination.class).value()))
                    .findFirst().ifPresent(field -> {
                        String filterTableName = extractTableName(filterClass);
                        String filterColumnName = field.getAnnotation(Column.class).name();
                        rightJoin.append(
                                  " RIGHT JOIN " + filterTableName + " ON "
                                + filterTableName + "." + filterColumnName + " = " +tableName + ".ID "
                        );
            });

            sql += rightJoin.toString();
        }

        sql += " WHERE 1 = 1 ";

        List<Field> currentUserId = ClassUtils.getFieldsByAnnotation(parentTableClass, CurrentUserId.class);
        String userIdColumnName = currentUserId.get(0).getAnnotation(CurrentUserId.class).columnName();

        if (currentUserId.size() == 1) {
            if (currentUserIdProvider != null && currentUserIdProvider.getCurrentUserId() != null) {
                sql += " AND " + parentTableName + "." + userIdColumnName + " = '" + currentUserIdProvider.getCurrentUserId() + "'";

            }
        }

        return sql;
    }

    public static <T> String generateSelectAllQueryWithForeigns(Class<T> type, String tableName) throws AnnotationException {

        String select = "SELECT " + tableName + ".*";
        String from = " FROM " + tableName;

        for (Field foreign : ClassUtils.getFieldsByAnnotation(type, Foreign.class)) {
            String foreignId = foreign.getAnnotation(Foreign.class).foreignId();
            String linkColumnName = ClassUtils.getFieldByName(type, foreignId).getAnnotation(Column.class).name();

            Class<?> foreignTableClass = foreign.getAnnotation(Foreign.class).table();
            String foreignTableName = extractTableName(foreignTableClass);
            String foreignColumnName = ClassUtils
                    .getFieldsByAnnotation(foreignTableClass, Label.class).get(0)
                    .getAnnotation(Column.class).name();

            String addSelect = ", " + foreignTableName + "." + foreignColumnName + " " + foreignTableName + "_" + foreignColumnName;

            select += addSelect;

            String leftJoin = " LEFT JOIN " + foreignTableName +
                    " ON " + tableName+"."+linkColumnName +
                    " = " + foreignTableName+".ID";

            from += leftJoin;
        }

        String sql = select + from;

        return sql;
    }

    public static UnnamedParametersQuery generateSelectSimilarQuery(Object obj) throws AnnotationException {

        Class<?> type = obj.getClass();

        Map<String, Object> columnsToValues = getColumnToValues(obj);

        String sql = generateSelectSimilarQuerySql(type, extractTableName(type), columnsToValues);
        List<Object> values = new ArrayList<>(columnsToValues.size());

        columnsToValues.keySet().forEach(col -> {
            values.add(columnsToValues.get(col));
        });

        return new UnnamedParametersQuery(sql, values);
    }

    private static <T> String generateSelectSimilarQuerySql(Class<T> type, String tableName, Map<String, Object> columnsToValues) {

        String mainPart = generateSelectAllQueryWithForeigns(type, tableName);

        String tail = " WHERE 1 = 1";
        for (String column : columnsToValues.keySet()) {
            tail += " AND " + column + " = ?";
        }

        return mainPart + tail;
    }

    public static UnnamedParametersQuery generateSelectCountSimilarQuery(Object obj) throws AnnotationException {

        Map<String, Object> columnsToValues = getColumnToValues(obj);

        String sql = generateSelectCountSimilarQuerySql(extractTableName(obj.getClass()), columnsToValues);
        List<Object> values = new ArrayList<>(columnsToValues.size());

        columnsToValues.keySet().forEach(col -> {
            values.add(columnsToValues.get(col));
        });

        return new UnnamedParametersQuery(sql, values);
    }

    private static String generateSelectCountSimilarQuerySql(String tableName, Map<String, Object> columnsToValues) {

        String mainPart = "SELECT COUNT(*) FROM " + tableName +
                         " WHERE 1 = 1";

        String tail = "";
        for (String column : columnsToValues.keySet()) {
            tail += " AND " + column + " = ?";
        }

        return mainPart + tail;
    }

    public static UnnamedParametersQuery generateInsertQuery(Object obj) throws AnnotationException {

        Map<String, Object> columnsToValues = getColumnToValues(obj);

        String sql = generateInsertQuerySql(extractTableName(obj.getClass()), columnsToValues);
        List<Object> values = new ArrayList<>(columnsToValues.size());

        columnsToValues.keySet().forEach(col -> {
            values.add(columnsToValues.get(col));
        });

        return new UnnamedParametersQuery(sql, values);
    }

    private static String generateInsertQuerySql(String tableName, Map<String, Object> columnsToValues) {

        int columnsNumber = columnsToValues.size();

        List<String> columns = new ArrayList<>(columnsNumber);

        columnsToValues.keySet().forEach(col -> {
            columns.add(col);
        });

        String columnsPart = columns.stream().sequential().collect(Collectors.joining(", ", "(", ")"));

        List<String> questMarks = new ArrayList<>();
        for (int i = 0; i < columnsNumber; i++) {
            questMarks.add("?");
        }

        String questMarksPart = questMarks.stream().collect(Collectors.joining(", ", "(", ")"));

        return "INSERT INTO " + tableName + " " + columnsPart + " VALUES " + questMarksPart;
    }

    public static UnnamedParametersQuery generateUpdateQuery(Object obj) throws AnnotationException {

        Map<String, Object> columnsToValues = getColumnToValues(obj);
        String idColumn = extractIdColumnName(obj);
        Object idValue = columnsToValues.get(idColumn);

        if (idValue == null) {
            throw new IllegalStateException("Updated model must have id!");
        }

        columnsToValues.remove(idColumn);//Must be last in WHERE clause

        String sql = generateUpdateQuerySql(extractTableName(obj.getClass()), columnsToValues);
        List<Object> values = new ArrayList<>(columnsToValues.size());

        columnsToValues.keySet().forEach(col -> {
            values.add(columnsToValues.get(col));
        });

        values.add(idValue);//Must be last in WHERE clause

        System.out.println("Update query: " + sql);

        return new UnnamedParametersQuery(sql, values);
    }

    private static String generateUpdateQuerySql(String tableName, Map<String, Object> columnsToValues) {

        String setPart = columnsToValues.keySet().stream()
                .map(col -> col + " = ?").collect(Collectors.joining(", "));

        return "UPDATE " + tableName + " SET " + setPart + " WHERE ID = ?";
    }

    public static UnnamedParametersQuery generateDeleteQuery(Object obj) throws AnnotationException {
        Map<String, Object> columnsToValues = getColumnToValues(obj);
        String idColumn = extractIdColumnName(obj);
        Object idValue = columnsToValues.get(idColumn);

        String sql = generateDeleteQuerySql(extractTableName(obj.getClass()), idColumn);
        List<Object> values = new ArrayList<>(columnsToValues.size());
        values.add(idValue);

        System.out.println("Delete query: " + sql);

        return new UnnamedParametersQuery(sql, values);
    }

    private static String generateDeleteQuerySql(String tableName, String idColumn) {
        return "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";
    }

    public static UnnamedParametersQuery generateDeleteSimilarQuery(Object obj) throws AnnotationException {

        Map<String, Object> columnsToValues = getColumnToValues(obj);

        if (columnsToValues.isEmpty()) {
            throw new IllegalArgumentException(
                    "Model is empty. " +
                    "Probably some fields are supposed to be set, but are not. " +
                    "Check it in advance.");
        }

        String sql = generateDeleteSimilarQuerySql(extractTableName(obj.getClass()), columnsToValues);
        List<Object> values = new ArrayList<>(columnsToValues.size());

        columnsToValues.keySet().forEach(col -> {
            values.add(columnsToValues.get(col));
        });

        return new UnnamedParametersQuery(sql, values);
    }

    private static String generateDeleteSimilarQuerySql(String tableName, Map<String, Object> columnsToValues) {

        String mainPart = "DELETE FROM " + tableName +
                " WHERE 1 = 1";

        String tail = "";
        for (String column : columnsToValues.keySet()) {
            tail += " AND " + column + " = ?";
        }

        return mainPart + tail;
    }

    private static Map<String, Object> getColumnToValues(Object obj) {

        Class<?> type = obj.getClass();
        Map<String, Object> columnToValues = new TreeMap<>();

        ClassUtils.getFieldsUpToObject(type).forEach(field -> {
                    field.setAccessible(true);
                    Object value = null;
                    try {
                        value = field.get(obj);
                    } catch (IllegalAccessException ignored) {}

                    if (field.isAnnotationPresent(Column.class) && value != null) {

                        columnToValues.put(field.getAnnotation(Column.class).name(), value);

                    } else if (field.isAnnotationPresent(CurrentUserId.class)
                            && currentUserIdProvider != null
                            && currentUserIdProvider.getCurrentUserId() != null) {

                        columnToValues.put(field.getAnnotation(CurrentUserId.class).columnName(), currentUserIdProvider.getCurrentUserId());

                    } else if (field.isAnnotationPresent(CurrentUserId.class)
                            && (currentUserIdProvider == null || currentUserIdProvider.getCurrentUserId() == null)) {

                        columnToValues.put(field.getAnnotation(CurrentUserId.class).columnName(), value);
                    }
                });
        return columnToValues;
    }

    private static String extractIdColumnName(Object obj) {

        return extractIdColumnName(obj.getClass());
    }

    private static String extractIdColumnName(Class<?> type) {

        String idColumn = null;

        List<Field> allFields = ClassUtils.getFieldsUpToObject(type);

        for (Field field : allFields) {

            field.setAccessible(true);

            if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(Id.class)) {
                idColumn = field.getAnnotation(Column.class).name();
            }

        }
        return idColumn;
    }

    public static String extractTableName(Class<?> type) throws AnnotationException {

        if (!type.isAnnotationPresent(Table.class)) {
            throw new TableAnnotationException("Аннотация @Table не найдена");
        }

        String tableName = type.getAnnotation(Table.class).name();
        if ("".equals(tableName)) tableName = type.getAnnotation(Table.class).value();

        if (!isProperDbEntityName(tableName)) {
            throw new AnnotationException("Недопустимое имя для таблицы базы данных.");
        }

        return tableName;
    }

    private static boolean isProperDbEntityName(String name) {
        return name != null && Pattern.matches("[a-zA-Z0-9_]+", name);
    }

    public static UnnamedParametersQuery createGetTableQuery(Class<?> klass) {

        String userId = currentUserIdProvider != null ? currentUserIdProvider.getCurrentUserId() : null;
//        SelectAllQueryGenerator generator = new SelectAllQueryGenerator(klass, "1234567", "qqwe");


//        System.out.println(generator.generateSelectAllQuery());

        return new UnnamedParametersQuery("null", new ArrayList<>());
    }

    public static void main(String[] args) throws Exception {

        Texts t = new Texts();
        t.id = "100";
        t.text = "text";
        t.transcription = "trans";

        createGetTableQuery(t.getClass());

//        System.out.println(createGetTableQuery(t.getClass()));
    }
}

@Table("TEXT")
class Texts {

    @Id
    @Column("ID")
    String id;
    @Column("TEXT")
    String text;
    @Column("TRANSCRIPTION")
    String transcription;
    @Column(value = "PARENT_ID", foreign = Parent.class)
    String parentId;
    @Column(value = "PARENT_ID2", foreign = Parent2.class)
    String parentId2;

    @Foreign2
    Parent parent;
}

@Table("PARENT")
class Parent {
    @Id
    @Column("ID")
    String id;
    @Column("NAME")
    String name;
}

@Table("PARENT2")
class Parent2 {
    @Id
    @Column("ID2")
    String id;
    @Column("NAME2")
    String name;
}