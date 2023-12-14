package io.github.miracelwhipp.javac.extension.compiler.plugin;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskListener;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.lang.reflect.InvocationTargetException;

public abstract class ReflectiveCompilerPlugin implements Plugin {

    @Override
    public String getName() {

        PluginName annotation = getClass().getAnnotation(PluginName.class);

        if (annotation != null) {

            return annotation.value();
        }

        return getClass().getCanonicalName();
    }

    @Override
    public void init(JavacTask javacTask, String... strings) {

        JavaCompilerTaskListener[] taskListeners = getClass().getAnnotationsByType(JavaCompilerTaskListener.class);

        for (JavaCompilerTaskListener taskListener : taskListeners) {

            try {

                TaskListener listener = taskListener.value().getDeclaredConstructor().newInstance();

                CmdLineParser parser = new CmdLineParser(listener);

                parser.parseArgument(strings);

                javacTask.addTaskListener(listener);

            } catch (CmdLineException e) {

                throw new IllegalArgumentException(e);

            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {

                throw new IllegalStateException(e);
            }
        }
    }
}