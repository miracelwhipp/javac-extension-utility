package io.github.miracelwhipp.javac.extension.compiler.plugin;

import com.sun.source.util.TaskEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a method in a {@link ReflectiveJavaCompilerTaskListener} as an after compiler task event handler.
 * An after compiler task event handler is a void method that accepts one parameter of type com.sun.source.util.TaskEvent.
 * It will be called after each event of the specified task event kind.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface After {

    /**
     * This property specifies the kind of event the annotated method is invoked for.
     */
    TaskEvent.Kind value();
}
