package kpersistence.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    String name() default "";
    String value() default "";
    Class<?> foreign() default Object.class;
    String type() default ColumnType.ORDINARY;
    boolean nonNull() default false;
}
