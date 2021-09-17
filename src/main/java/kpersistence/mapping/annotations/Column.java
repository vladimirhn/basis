package kpersistence.mapping.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    String name();
    String rus() default "";
    boolean isAncillary() default false;

}
