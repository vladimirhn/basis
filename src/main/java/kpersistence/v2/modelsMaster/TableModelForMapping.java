package kpersistence.v2.modelsMaster;

import kpersistence.v2.annotations.Column;
import kpersistence.v2.annotations.Foreign2;
import kpersistence.v2.annotations.PersistenceAnnotationsUtils;
import kutils.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class TableModelForMapping {

    private final List<FieldProperties> mainFieldPropertiesList;
    private final Map<Class<?>, List<FieldProperties>> parentClassPropertiesMap = new LinkedHashMap<>();
    private final Map<Class<?>, Field> parentClassToMainFieldMap = new HashMap<>();

    public List<FieldProperties> getMainBunchList() {
        return mainFieldPropertiesList;
    }

    public Map<Class<?>, List<FieldProperties>> getParentClassPropertiesMap() {
        return parentClassPropertiesMap;
    }

    public Map<Class<?>, Field> getParentClassToMainFieldMap() {
        return parentClassToMainFieldMap;
    }

    public TableModelForMapping(Class<?> mainClass) {

        mainFieldPropertiesList = makePropertiesList(mainClass);

        extractClasses(mainClass).forEach(klass -> {
            List<FieldProperties> fieldProperties = makePropertiesList(klass);
            parentClassPropertiesMap.put(klass, fieldProperties);
        });
    }

    private List<FieldProperties> makePropertiesList(Class<?> klass) {
        return ClassUtils.getFieldsUpToObject(klass).stream()
                .map(field -> makeProperties(field, PersistenceAnnotationsUtils.extractTableName(klass)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private FieldProperties makeProperties(Field field, String tableName) {

        FieldProperties fieldProperties = null;

        if (field.isAnnotationPresent(Column.class)) {
            field.setAccessible(true);

            String columnName = field.getAnnotation(Column.class).name();
            fieldProperties = new FieldProperties(tableName + "__" + columnName, field);
        }

        return fieldProperties;
    }

    private List<Class<?>> extractClasses(Class<?> mainClass) {

        List<Class<?>> result = new ArrayList<>();

        ClassUtils.getFieldsUpToObject(mainClass).forEach(field -> {
            if (field.isAnnotationPresent(Column.class)) {
                Class<?> foreign = field.getAnnotation(Column.class).foreign();
                if (foreign != Object.class) {
                    result.add(foreign);
                    mapParentClassToField(foreign, mainClass);
                }
            }
        });

        return result;
    }

    private void mapParentClassToField(Class<?> foreign, Class<?> mainClass) {
        ClassUtils.getFieldsUpToObject(mainClass).forEach(field -> {
            if (field.isAnnotationPresent(Foreign2.class) && field.getType() == foreign) {
                field.setAccessible(true);
                parentClassToMainFieldMap.put(foreign, field);
            }
        });
    }

    public boolean checkParentForNonNullFields(Object parentObject) {
        List<FieldProperties> parentFieldsProps = parentClassPropertiesMap.get(parentObject.getClass());
        return checkObjectNonNullFields(parentObject, parentFieldsProps);
    }

    private boolean checkObjectNonNullFields(Object parentObject, List<FieldProperties> parentFieldsProps) {
        try {

            for (FieldProperties props : parentFieldsProps) {
                if (props.field.isAnnotationPresent(Column.class) && props.field.get(parentObject) != null) {
                    return true;
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
}
