package io.github.miracelwhipp.javac.extension.annotation.processor;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a method in a {@link ReflectiveAnnotationProcessor} as a round completion handler. A round
 * completion handler is a void-method without any parameters. It will be called after any annotation processing
 * round finished.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoundCompletionHandler {
}
