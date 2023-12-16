package io.github.miracelwhipp.javac.extension.compiler.plugin;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskListener;
import io.github.miracelwhipp.javac.extension.configuration.CommandLine;
import io.github.miracelwhipp.javac.extension.configuration.ConfigurationPropertyParsers;
import io.github.miracelwhipp.javac.extension.configuration.Configurator;

import java.lang.reflect.InvocationTargetException;

/**
 * This class implements the core java compiler task listener registration. To use it define a subclass of it. More
 * documentation can be found
 * <a href="https://miracelwhipp.github.io/javac-extension-utility/#_writing_compiler_plugins">here</a>.
 */
public abstract class ReflectiveCompilerPlugin implements Plugin {

    private JavacTask task;

    @Override
    public String getName() {

        PluginName annotation = getClass().getAnnotation(PluginName.class);

        if (annotation != null) {

            return annotation.value();
        }

        return getClass().getCanonicalName();
    }

    public JavacTask getTask() {

        return task;
    }

    @Override
    public void init(JavacTask javacTask, String... strings) {

        try {

            task = javacTask;

            CommandLine commandLine = CommandLine.parse(strings);

            Configurator<ReflectiveCompilerPlugin> configurator = commandLine.configuratorForClass(getClass());
            configurator.configure(this);

            CompilerTaskEventListenerRegistry listenerRegistry = CompilerTaskEventListenerRegistry.forInstance(this);

            if (!listenerRegistry.isEmpty()) {

                javacTask.addTaskListener(new RegistryBasedTaskListener(listenerRegistry, task));
            }

            JavaCompilerTaskListener[] taskListeners = getClass().getAnnotationsByType(JavaCompilerTaskListener.class);

            for (JavaCompilerTaskListener taskListener : taskListeners) {

                TaskListener listener = taskListener.value().getDeclaredConstructor().newInstance();

                if (listener instanceof TaskAware) { // TODO: decide whether to drop java 11 support

                    ((TaskAware) listener).setTask(task);
                }

                commandLine.configuratorForClass(listener.getClass()).configure(ConfigurationPropertyParsers.cast(listener));

                javacTask.addTaskListener(listener);
            }

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {

            throw new IllegalStateException(e);
        }
    }
}
