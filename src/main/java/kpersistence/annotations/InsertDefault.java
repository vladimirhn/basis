package kpersistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InsertDefault {
    String s() default "";
    boolean b() default false;
    int i() default 0;
    long l() default 0;
    double d() default 0.0;
    String date() default "1970-01-01";
    String dateTime() default "1970-01-01T00:00:00";
}
