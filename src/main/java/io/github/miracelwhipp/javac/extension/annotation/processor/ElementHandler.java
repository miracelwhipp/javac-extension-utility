package io.github.miracelwhipp.javac.extension.annotation.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a method in a {@link ReflectiveAnnotationProcessor} as an element handler. An element handler
 * is a void method that accepts two parameters. The first one being an annotation type it is registered to and the
 * second one being the element type it is registered to. The element handler will be called for every element of the
 * given element type that is annotated with the given annotation type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ElementHandler {
}
