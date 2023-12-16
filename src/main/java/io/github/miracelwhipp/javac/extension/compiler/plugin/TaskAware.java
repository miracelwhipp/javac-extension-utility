package io.github.miracelwhipp.javac.extension.compiler.plugin;

import com.sun.source.util.JavacTask;

public interface TaskAware {

    void setTask(JavacTask task);

}
