package org.example.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Qualifier;

@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Qualifier
public @interface CacheBean {
    String value() default "";
}
