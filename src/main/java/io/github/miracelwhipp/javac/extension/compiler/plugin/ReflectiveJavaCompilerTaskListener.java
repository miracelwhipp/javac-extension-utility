package io.github.miracelwhipp.javac.extension.compiler.plugin;

import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class implements the core java compiler task listener invocation. To use it define a subclass of it. More
 * documentation can be found
 * <a href="https://miracelwhipp.github.io/javac-extension-utility/">here</a>.
 */
public abstract class ReflectiveJavaCompilerTaskListener implements TaskListener {

    private final Map<TaskEvent.Kind, Consumer<TaskEvent>> startedHandlers;
    private final Map<TaskEvent.Kind, Consumer<TaskEvent>> finishedHandlers;

    protected ReflectiveJavaCompilerTaskListener() {
        this.startedHandlers = new LinkedHashMap<>();
        this.finishedHandlers = new LinkedHashMap<>();

        readEventHandlers(getClass());
    }

    private void readEventHandlers(Class<?> clazz) {

        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null && superclass != Object.class) {

            readEventHandlers(superclass);
        }

        for (Class<?> interfaze : clazz.getInterfaces()) {

            readEventHandlers(interfaze);
        }

        for (Method method : clazz.getDeclaredMethods()) {

            if (Modifier.isAbstract(method.getModifiers())) {

                continue;
            }

            Before annotation = method.getAnnotation(Before.class);

            Map<TaskEvent.Kind, Consumer<TaskEvent>> handlers = startedHandlers;

            TaskEvent.Kind kind;

            if (annotation == null) {

                After afterAnnotation = method.getAnnotation(After.class);

                if (afterAnnotation == null) {

                    continue;
                }

                handlers = finishedHandlers;
                kind = afterAnnotation.value();

            } else {

                kind = annotation.value();
            }


            ReflectiveJavaCompilerTaskListener me = this;

            handlers.put(kind, event -> {

                try {

                    method.invoke(me, event);

                } catch (IllegalAccessException | InvocationTargetException e) {

                    throw new IllegalStateException(e);
                }
            });
        }
    }


    private Consumer<TaskEvent> getEventHandler(Map<TaskEvent.Kind, Consumer<TaskEvent>> handlerMap, TaskEvent.Kind eventKind) {

        return handlerMap.computeIfAbsent(eventKind, key -> x -> {
        });
    }

    @Override
    public void started(TaskEvent event) {

        getEventHandler(startedHandlers, event.getKind()).accept(event);
    }

    @Override
    public void finished(TaskEvent event) {

        getEventHandler(finishedHandlers, event.getKind()).accept(event);
    }
}
