package io.github.miracelwhipp.javac.extension.compiler.plugin;

import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

public class RegistryBasedTaskListener implements TaskListener, TaskAware {
    protected CompilerTaskEventListenerRegistry registry;
    private JavacTask task;

    public RegistryBasedTaskListener(CompilerTaskEventListenerRegistry registry, JavacTask task) {
        this.registry = registry;
        this.task = task;
    }

    protected RegistryBasedTaskListener() {
    }

    @Override
    public void setTask(JavacTask task) {

        this.task = task;
    }

    public JavacTask getTask() {
        return task;
    }

    @Override
    public void started(TaskEvent event) {

        registry.before().listen(event);
    }

    @Override
    public void finished(TaskEvent event) {

        registry.after().listen(event);
    }
}
