package kpersistence.v1.mapping.rowMappers;

import kpersistence.v2.annotations.Column;
import kpersistence.v1.mapping.annotations.Foreign;
import kpersistence.v2.annotations.Label;
import kpersistence.v2.annotations.Table;
import kutils.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KRowMapper<T> {

    Class<T> type;

    public KRowMapper(Class<T> type) {
        this.type = type;
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            for (int x = 1; x <= columns; x++) {
                if (columnName.equalsIgnoreCase(rsmd.getColumnName(x))) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Возвращает поля, аннотации @Column и @Foreign которых указывают на колонки, присутствующие в ResultSet
     */
    private Map<Field, String> getFieldsThatMatchResultSet(Class<T> type, ResultSet rs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Map<Field, String> result = new HashMap<>();

        List<Field> fields = ClassUtils.getFieldsUpToObject(type);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Foreign.class)) {

                String columnName = null;

                if (field.isAnnotationPresent(Column.class)) {

                    columnName = field.getAnnotation(Column.class).name();

                } else if (field.isAnnotationPresent(Foreign.class)) {

                    Class<?> foreignTableClass = field.getAnnotation(Foreign.class).table();
                    String foreignTableName = foreignTableClass.getAnnotation(Table.class).name();
                    String foreignColumnName = ClassUtils
                            .getFieldsByAnnotation(foreignTableClass, Label.class).get(0)
                            .getAnnotation(Column.class).name();

                    columnName = foreignTableName + "_" + foreignColumnName;

                }
                if (columnName != null && hasColumn(rs, columnName)) {
                    result.put(field, columnName);
                }
            }
        }

        return result;
    }

//    @Override
    public T mapRow(ResultSet rs, int i) {
        try {
            T obj = type.getDeclaredConstructor().newInstance();
            Map<Field, String> fieldsAndColumnNames = getFieldsThatMatchResultSet(type, rs);

            for (Field field : fieldsAndColumnNames.keySet()) {

                field.setAccessible(true);

                Object data = rs.getObject(fieldsAndColumnNames.get(field));

                Class<?> fieldType = field.getType();
                Class<?> dataType = data != null ? data.getClass() : Object.class;

                if (fieldType.equals(LocalDate.class) && dataType.equals(Timestamp.class)) {
                    field.set(obj, ((Timestamp) data).toLocalDateTime().toLocalDate());
                }

                if (fieldType.equals(LocalDateTime.class) && dataType.equals(Timestamp.class)) {
                    field.set(obj, ((Timestamp) data).toLocalDateTime());
                }

                if (fieldType.equals(Long.class) && dataType.equals(Integer.class)) {
                    field.set(obj, (long) (int) data);
                }

                if (fieldType.equals(BigDecimal.class)) {
                    if (data != null) {
                        field.set(obj, new BigDecimal(String.valueOf(data)));
                    }
                }

                if (fieldType.equals(Enum.class)) {
                    if (data != null) {
                        Enum<?> enumObject = (Enum<?>) field.get(obj);
                        Class<? extends Enum> enumType = enumObject.getClass();
                        Enum<?> value = Enum.valueOf(enumType, data.toString());
                        field.set(obj, value);
                    }
                }

                //Sqlite
                // boolean processing
                if (fieldType.equals(Boolean.class)) {
                    if ("1".equals(String.valueOf(data))) {
                        field.set(obj, true);
                    }
                    if ("0".equals(String.valueOf(data))) {
                        field.set(obj, false);
                    }
                }
                //LocalDate(Time)
                if (fieldType.equals(LocalDate.class) && dataType.equals(String.class)) {
                    field.set(obj, LocalDate.parse(data.toString()));
                }

                if (fieldType.equals(LocalDateTime.class) && dataType.equals(String.class)) {
                    field.set(obj, LocalDateTime.parse(data.toString()));
                }

                //default
                if (fieldType.isAssignableFrom(dataType)) {
                    field.set(obj, data);
                }
            }

            return obj;

        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("Не удаётся обработать модель. У модели обязан быть конструктор без параметров.");

        } catch (SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Не удаётся обработать модель. См. детали в логе.");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new IllegalArgumentException("Не удаётся обработать модель. См. детали в логе.");
        }
    }
}
