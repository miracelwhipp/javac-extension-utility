package io.github.miracelwhipp.javac.extension.compiler.plugin;

import com.sun.source.util.TaskEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CompilerTaskEventListenerRegistry {

    private final TaskListenerRegistry beforeListeners = new TaskListenerRegistry();
    private final TaskListenerRegistry afterListeners = new TaskListenerRegistry();

    public TaskListenerRegistry before() {
        return beforeListeners;
    }

    public TaskListenerRegistry after() {
        return afterListeners;
    }

    public boolean isEmpty() {

        return beforeListeners.isEmpty() && afterListeners.isEmpty();
    }

    public static CompilerTaskEventListenerRegistry forInstance(Object instance) {

        CompilerTaskEventListenerRegistry compilerTaskEventListenerRegistry = new CompilerTaskEventListenerRegistry();

        appendHandlers(instance.getClass(), instance, compilerTaskEventListenerRegistry);

        return compilerTaskEventListenerRegistry;
    }

    public static void appendHandlers(Class<?> clazz, Object instance, CompilerTaskEventListenerRegistry registry) {

        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null && superclass != Object.class) {

            appendHandlers(superclass, instance, registry);
        }

        for (Class<?> interfaze : clazz.getInterfaces()) {

            appendHandlers(interfaze, instance, registry);
        }

        for (Method method : clazz.getDeclaredMethods()) {

            if (Modifier.isAbstract(method.getModifiers())) {

                continue;
            }

            Before annotation = method.getAnnotation(Before.class);

            TaskListenerRegistry listenerRegistry = registry.before();

            TaskEvent.Kind kind;

            if (annotation == null) {

                After afterAnnotation = method.getAnnotation(After.class);

                if (afterAnnotation == null) {

                    continue;
                }

                listenerRegistry = registry.after();
                kind = afterAnnotation.value();

            } else {

                kind = annotation.value();
            }


            listenerRegistry.addListener(kind, event -> {

                try {

                    method.invoke(instance, event);

                } catch (IllegalAccessException | InvocationTargetException e) {

                    throw new IllegalStateException(e);
                }
            });
        }
    }

    public static class TaskListenerRegistry {

        private final Map<TaskEvent.Kind, List<Consumer<TaskEvent>>> listeners = new LinkedHashMap<>();

        public void addListener(TaskEvent.Kind taskKind, Consumer<TaskEvent> listener) {

            getListeners(taskKind).add(listener);
        }

        public List<Consumer<TaskEvent>> getListeners(TaskEvent.Kind taskKind) {

            return listeners.computeIfAbsent(taskKind, key -> new ArrayList<>());
        }

        public void listen(TaskEvent event) {

            getListeners(event.getKind()).forEach(listener -> listener.accept(event));
        }

        public boolean isEmpty() {

            return listeners.isEmpty();
        }
    }

}
