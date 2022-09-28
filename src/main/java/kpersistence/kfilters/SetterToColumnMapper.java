package kpersistence.kfilters;

import kpersistence.annotations.Column;
import kutils.ClassUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetterToColumnMapper<T> {

    private List<Object> labels = new ArrayList<>();
    private RamMap labelToColumn = new RamMap();

    public <V extends Object> String map(Class<T> clazz, BiConsumer<T, V> setter, Class<V> type) {
        try {

            if (setter == null) {
                throw new IllegalArgumentException("Some setters need to be provided!");
            }

            T instance = clazz.newInstance();

            String fieldTypeName = extractFieldTypeName(instance, setter, type);
            this.labels.add(setUpLabel(instance, setter, fieldTypeName));

            fillRamMap(clazz, instance);

            String colName = null;
            for (Object label : labels) {
                colName = labelToColumn.get(label);
            }
            return colName;

        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println(String.format("Не удалось создать экземпляр класса %s для функции отображения сеттеров в имена колонок.", clazz.getName()));
            e.printStackTrace();
        }

        return null;
    }

    @Deprecated
    @SafeVarargs
    public final List<String> map(Class<T> clazz, FieldRef<T, ?>... setters) {

        try {

            if (setters == null) {
                throw new IllegalArgumentException("Some setters need to be provided!");
            }

            T instance = clazz.newInstance();

            for (FieldRef fieldRef : setters) {

                BiConsumer setter = fieldRef.getSetter();

                String fieldTypeName = extractFieldTypeName(instance, setter, null);
                this.labels.add(setUpLabel(instance, setter, fieldTypeName));
            }

            fillRamMap(clazz, instance);

            List<String> result = new ArrayList<>();

            for (Object label : labels) {
                result.add(labelToColumn.get(label));
            }

            return result;

        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println(String.format("Не удалось создать экземпляр класса %s для функции отображения сеттеров в имена колонок.", clazz.getName()));
            e.printStackTrace();
        }

        return null;
    }

    public String extractFieldTypeName(T instance, BiConsumer reference, Class<?> type) {

        if (type != null) {
            return type.getCanonicalName();
        }

        try {
            reference.accept(instance, new Object());
        } catch (ClassCastException cce) {
            Matcher matcher = Pattern.compile("cannot be cast to (.*)$").matcher(cce.getMessage());
            if (matcher.find()) {

                return matcher.group(1);
            }
        }
        throw new RuntimeException("Something's wrong");
    }

    public <V> Object setUpLabel(T obj, BiConsumer setter, String fieldTypeName) {

        V label = getNewInstance(fieldTypeName);

        setter.accept(obj, label);

        return label;
    }

    private <V> V getNewInstance(String fieldTypeName) {

        if (fieldTypeName.equals("java.lang.Boolean")) {
            return (V) new Boolean(true);

        } else if (fieldTypeName.equals("java.lang.Integer")) {
            return (V) new Integer(0);

        } else if (fieldTypeName.equals("java.lang.Long")) {
            return (V) new Long(0L);

        } else if (fieldTypeName.equals("java.lang.Double")) {
            return (V) new Double(0D);

        } else if (fieldTypeName.equals("java.lang.Float")) {
            return (V) new Float(0F);

        } else if (fieldTypeName.equals("java.util.Date")) {
            return (V) new Date();

        } else if (fieldTypeName.equals("java.lang.String")) {
            return (V) new String();

        } else {

            throw new RuntimeException("Не удалось создать второй экземпляр класса значения фильтра для метки.");
        }
    }

    private void fillRamMap(Class<T> clazz, T instance) throws IllegalAccessException {
        List<Field> fields = ClassUtils.getFieldsUpToObject(clazz);
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.get(instance) != null) {

                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    labelToColumn.put(field.get(instance), column.name());
                }
            }
        }
    }
}
