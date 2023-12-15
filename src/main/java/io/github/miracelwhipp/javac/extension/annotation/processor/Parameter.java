package io.github.miracelwhipp.javac.extension.annotation.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation marks a field in a {@link ReflectiveAnnotationProcessor} as a configuration parameter. The field
 * must be of type string or a primitive type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter {

    /**
     * This property specifies the name of the configuration property.
     */
    String name();

    /**
     * This property specifies the default value of the configuration property.
     */
    String defaultValue() default "";
}
