package kpersistence.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Работает в паре с @Foreign, указывает какое поле прилинкованной таблицы считать
 *  неймом для айдишника этой таблицы
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Label {

}
