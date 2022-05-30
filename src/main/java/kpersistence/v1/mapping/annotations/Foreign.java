package kpersistence.v1.mapping.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Foreign {

    Class<?> table();
    String foreignId();
}
