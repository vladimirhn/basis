package kutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ClassUtils {

    public static List<Field> getFields(Class<?> type) {
        return Arrays.asList(type.getDeclaredFields());
    }

    public static List<Field> getFieldsUpToObject(Class<?> type) {
        List<Field> result = new ArrayList<>();
        return getFieldsUpToObject(result, type);
    }

    private static List<Field> getFieldsUpToObject(List<Field> result, Class<?> type) {
        if (type.getSuperclass() != null) {
            getFieldsUpToObject(result, type.getSuperclass());
        }
        result.addAll(Arrays.asList(type.getDeclaredFields()));
        return result;
    }

    public static List<Field> getFieldsByAnnotation(Class<?> type, Class<? extends Annotation> ann) {

        List<Field> fields = new LinkedList<>();

        getFieldsUpToObject(type).forEach(field -> {
            if (field.isAnnotationPresent(ann))
                fields.add(field);
        });
        return fields;
    }

    public static Field getFirstFieldByAnnotation(Class<?> type, Class<? extends Annotation> ann) {
        List<Field> fields = getFieldsByAnnotation(type, ann);

        if (fields.isEmpty()) {
            throw new IllegalArgumentException(type.getSimpleName() + "doesn't have a filed with " + ann.getSimpleName() + " annotation");
        }

        return fields.get(0);
    }

    public static Field getFieldByName(Class<?> type, String name) {

        Field result = null;

        for (Field field : getFieldsUpToObject(type)) {
            if (field.getName().equals(name)) {
                result = field;
                break;
            }
        }
        return result;
    }

    public static <E, T> T createInstanceByExample(E example, Class<T> instanceClass) {

        T instance = null;

        try {
            instance = instanceClass.getDeclaredConstructor().newInstance();

            for (Field exField : ClassUtils.getFieldsUpToObject(example.getClass())) {
                Field insField = ClassUtils.getFieldByName(instanceClass, exField.getName());

                if (insField != null && exField.getType().equals(insField.getType())) {
                    exField.setAccessible(true);
                    insField.setAccessible(true);

                    insField.set(instance, exField.get(example));
                }
            }

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(instanceClass.getSimpleName() + "doesn't have a no args public constructor");
        }

        return instance;
    }
}
