package io.github.miracelwhipp.javac.extension.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Argument {

    /**
     * This property specifies the default value of the configuration property.
     */
    String[] defaultValue() default {};
}
