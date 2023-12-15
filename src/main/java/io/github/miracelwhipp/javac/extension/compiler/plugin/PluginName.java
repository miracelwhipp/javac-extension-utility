package io.github.miracelwhipp.javac.extension.compiler.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies the name of a {@link ReflectiveCompilerPlugin}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginName {

    /**
     * This property specifies the name of the {@link ReflectiveCompilerPlugin} annotated.
     */
    String value();
}
