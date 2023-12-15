package io.github.miracelwhipp.javac.extension.compiler.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This method is the repeatable closure for {@link JavaCompilerTaskListener}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JavaCompilerTaskListeners {

    JavaCompilerTaskListener[] value();
}
