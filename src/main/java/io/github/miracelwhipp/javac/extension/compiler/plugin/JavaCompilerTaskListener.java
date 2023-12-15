package io.github.miracelwhipp.javac.extension.compiler.plugin;

import com.sun.source.util.TaskListener;

import java.lang.annotation.*;

/**
 * This annotation registers a task listener on a subclass of {@link ReflectiveCompilerPlugin}. Each task listener
 * registered will be added to the java task the compiler plugin receives.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(JavaCompilerTaskListeners.class)
public @interface JavaCompilerTaskListener {

    /**
     * This property specifies the task listener registered. The class must declare a public no-args constructor.
     */
    Class<? extends TaskListener> value();
}
