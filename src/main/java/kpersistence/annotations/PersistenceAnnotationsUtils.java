package kpersistence.annotations;

import kpersistence.exceptions.AnnotationException;
import kpersistence.exceptions.TableAnnotationException;

import java.util.regex.Pattern;

public class PersistenceAnnotationsUtils {

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
}
