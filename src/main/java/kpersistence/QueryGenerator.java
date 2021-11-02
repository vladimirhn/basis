package kpersistence;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import kpersistence.exceptions.AnnotationException;
import kpersistence.exceptions.TableAnnotationException;
import kpersistence.mapping.annotations.*;
import kutils.ClassUtils;

public class QueryGenerator {

    public static CurrentUserIdProvider currentUserIdProvider;

    public static UnnamedParametersQuery generateSelectOneQuery(String id, Class<?> type) throws AnnotationException {
        String tableName = extractTableName(type);
        String idColumn = extractIdColumnName(type);

        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";

        return new UnnamedParametersQuery(sql, Collections.singletonList(id));
    }

    public static <T> String generateSelectAllQuery(Class<T> type) throws AnnotationException {

        String tableName = extractTableName(type);

        String sql;

        if (ClassUtils.getFieldsByAnnotation(type, Foreign.class).isEmpty()) {
            sql = "SELECT * FROM " + tableName;
        } else {
            sql = generateSelectAllQueryWithForeigns(type, tableName);
        }

        List<Field> orders = ClassUtils.getFieldsByAnnotation(type, OrderBy.class);
        if (orders.size() == 1) {
            sql += " ORDER BY "
                    + orders.get(0).getAnnotation(Column.class).name()
                    + " "
                    + orders.get(0).getAnnotation(OrderBy.class).direction().name();
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

    private static String extractTableName(Class<?> type) throws AnnotationException {

        if (!type.isAnnotationPresent(Table.class)) {
            throw new TableAnnotationException("Аннотация @Table не найдена");
        }

        String tableName = type.getAnnotation(Table.class).name();

        if (!isProperDbEntityName(tableName)) {
            throw new AnnotationException("Недопустимое имя для таблицы базы данных.");
        }

        return tableName;
    }

    private static boolean isProperDbEntityName(String name) {
        return name != null && Pattern.matches("[a-zA-Z0-9_]+", name);
    }

//    public static void main(String[] args) throws Exception {
//
//        Texts t = new Texts();
//        t.id = 100;
//        t.text = "text";
//        t.rus = "rus";
//        t.transcription = "trans";
//
//        System.out.println(generateSelectCountSimilarQuery(t));
//    }
}

//@Table(name = "texts")
//class Texts {
//
//    @Id
//    @Column(name = "id")
//    Integer id;
//    @Column(name = "text")
//    String text;
//    @Column(name = "transcription")
//    String transcription;
//    @Column(name = "rus")
//    String rus;
//
//    public Texts() {
//    }
//}