package kpersistence.domain;

import java.util.Map;

public class Tables {

    private static Map<String, Class<?>> tables;

    public static void setTables(Map<String, Class<?>> tables) {

        if (Tables.tables != null) {
            throw new IllegalStateException("Tables already set");
        }

        Tables.tables = tables;
    }

    public static Class<?> getModelClassByName(String className) {
        return tables.get(className);
    }
}
