package kpersistence.modelsMaster.mapping;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FieldProperties {
    String columnHeader;
    Field field;
    SqlDataSetter dataSetter;

    FieldProperties(String columnHeader, Field field) {
        this.columnHeader = columnHeader;
        this.field = field;
        defineDataSetter();
    }

    void defineDataSetter() {
        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            dataSetter = this::stringDataSetter;

        } else if (fieldType == Long.class) {
            dataSetter = this::longDataSetter;

        } else if (fieldType == LocalDate.class) {
            dataSetter = this::localDateDataSetter;

        } else if (fieldType == LocalDateTime.class) {
            dataSetter = this::localDateTimeDataSetter;

        } else if (fieldType == BigDecimal.class) {
            dataSetter = this::bigDecimalDataSetter;

        } else if (fieldType == Boolean.class) {
            dataSetter = this::booleanDataSetter;

        } else {
            dataSetter = this::defaultDataSetter;
        }
    }

    private void stringDataSetter(ResultSet rs, Object mainDTO) {
        try {
            field.set(mainDTO, rs.getString(columnHeader));
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void longDataSetter(ResultSet rs, Object mainDTO) {
        try {
            field.set(mainDTO, rs.getLong(columnHeader));
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void localDateDataSetter(ResultSet rs, Object mainDTO) {
        try {
            Timestamp data = rs.getTimestamp(columnHeader);
            if (data != null) {
                field.set(mainDTO, data.toLocalDateTime().toLocalDate());
            }
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void localDateTimeDataSetter(ResultSet rs, Object mainDTO) {
        try {
            Timestamp data = rs.getTimestamp(columnHeader);
            if (data != null) {
                field.set(mainDTO, data.toLocalDateTime());
            }
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void bigDecimalDataSetter(ResultSet rs, Object mainDTO) {
        try {
            String data = rs.getString(columnHeader);
            if (data != null) {
                field.set(mainDTO, new BigDecimal(data));
            }
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void booleanDataSetter(ResultSet rs, Object mainDTO) {
        try {
            field.set(mainDTO, rs.getBoolean(columnHeader));
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void defaultDataSetter(ResultSet rs, Object mainDTO) {
        try {

            Object data = rs.getObject(columnHeader);
            Class<?> dataType = data != null ? data.getClass() : Object.class;

            if (field.getType().isAssignableFrom(dataType)) {
                field.set(mainDTO, data);
            }

        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    public String getColumnHeader() {
        return columnHeader;
    }

    public Field getField() {
        return field;
    }

    public SqlDataSetter getDataSetter() {
        return dataSetter;
    }

    public void setData(ResultSet rs, Object mainDTO) {
        dataSetter.setData(rs, mainDTO);
    }
}
