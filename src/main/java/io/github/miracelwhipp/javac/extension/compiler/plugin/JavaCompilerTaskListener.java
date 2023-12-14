package io.github.miracelwhipp.javac.extension.compiler.plugin;

import com.sun.source.util.TaskListener;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(JavaCompilerTaskListeners.class)
public @interface JavaCompilerTaskListener {

    Class<? extends TaskListener> value();
}
