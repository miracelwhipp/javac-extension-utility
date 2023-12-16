package io.github.miracelwhipp.javac.extension.annotation.processor;

import io.github.miracelwhipp.javac.extension.configuration.ConfigurationPropertyParsers;
import io.github.miracelwhipp.javac.extension.configuration.Parameter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class implements the core annotation processing registration. To use it define a subclass of it. More
 * documentation can be found
 * <a href="https://miracelwhipp.github.io/javac-extension-utility/#_using_javac_extension_utilities">here</a>.
 */
public abstract class ReflectiveAnnotationProcessor extends AbstractProcessor {

    private final Map<String, Map<Class<?>, BiConsumer<Annotation, Element>>> handlers = new LinkedHashMap<>();

    private final Map<String, Class<? extends Annotation>> annotationClassesByName = new LinkedHashMap<>();

    private final List<Runnable> initializers = new ArrayList<>();
    private final List<Runnable> roundInitializers = new ArrayList<>();
    private final List<Runnable> roundCompletions = new ArrayList<>();
    private final List<Runnable> completions = new ArrayList<>();

    private final Map<String, Consumer<ProcessingEnvironment>> optionSetters = ConfigurationPropertyParsers.parameterSettersForInstance(this);

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        super.init(processingEnv);

        for (Consumer<ProcessingEnvironment> option : optionSetters.values()) {

            option.accept(processingEnv);
        }

        scanClassForHandlers(getClass());

        for (Runnable initializer : initializers) {

            initializer.run();
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {

        return annotationClassesByName.keySet();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return optionSetters.keySet();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        // do not run round initializers in last round
        if (!annotations.isEmpty()) {

            for (Runnable roundInitializer : roundInitializers) {

                roundInitializer.run();
            }
        }

        for (TypeElement annotationElement : annotations) {

            String annotationClassName = annotationElement.getQualifiedName().toString();
            Class<? extends Annotation> annotationClass = annotationClassesByName.get(annotationClassName);

            Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(annotationElement);

            for (Element element : annotatedElements) {

                Map<Class<?>, BiConsumer<Annotation, Element>> handlersByElementType = handlers.computeIfAbsent(annotationClassName, key -> new LinkedHashMap<>());

                BiConsumer<Annotation, Element> handler = handlersByElementType.get(element.getClass());

                if (handler == null) {

                    Optional<Class<?>> first = handlersByElementType.keySet().stream().filter(clazz -> clazz.isAssignableFrom(element.getClass())).findFirst();

                    if (first.isPresent()) {

                        handler = handlersByElementType.get(first.get());

                        handlersByElementType.put(element.getClass(), handler);
                    }
                }

                if (handler == null) {

                    continue;
                }

                Annotation annotation = element.getAnnotation(annotationClass);

                handler.accept(annotation, element);

            }
        }

        if (annotations.isEmpty()) {

            for (Runnable completion : completions) {

                completion.run();
            }

        } else {

            for (Runnable roundCompletion : roundCompletions) {

                roundCompletion.run();
            }
        }

        return true;
    }

    private void scanClassForHandlers(Class<?> clazz) {

        Method[] methods = clazz.getDeclaredMethods();

        // adding the completion handlers before visiting the super class in order to execute the completion handlers
        // of the subclass before the completion handlers.
        roundCompletions.addAll(getRunnableHandlers(methods, RoundCompletionHandler.class));
        completions.addAll(getRunnableHandlers(methods, CompletionHandler.class));

        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null && !superclass.equals(Object.class)) {

            scanClassForHandlers(superclass);
        }

        addElementHandlers(methods);

        // adding the initialize handler after visiting the super class in order to execute them super class first.
        initializers.addAll(getRunnableHandlers(methods, InitializeHandler.class));
        roundInitializers.addAll(getRunnableHandlers(methods, RoundInitializeHandler.class));
    }

    private void addElementHandlers(Method[] methods) {
        for (Method method : methods) {

            if (method.getAnnotation(ElementHandler.class) == null) {

                continue;
            }

            if (method.getParameters().length != 2) {

                continue;
            }

            java.lang.reflect.Parameter parameter1 = method.getParameters()[0];

            Class<?> annotationClass = parameter1.getType();

            if (!(Annotation.class.isAssignableFrom(annotationClass))) {

                continue;
            }

            java.lang.reflect.Parameter parameter2 = method.getParameters()[1];

            if (!(Element.class.isAssignableFrom(parameter2.getType()))) {

                continue;
            }

            addAnnotationClass(annotationClass);

            if (handlers.computeIfAbsent(annotationClass.getCanonicalName(), key -> new LinkedHashMap<>()).put(parameter2.getType(), ((annotation, element) -> {

                try {

                    method.invoke(this, annotation, element);

                } catch (IllegalAccessException e) {

                    throw new RuntimeException(e);

                } catch (InvocationTargetException e) {

                    if (e.getCause() instanceof RuntimeException) {

                        throw (RuntimeException) e.getCause();
                    }

                    throw new RuntimeException(e);
                }
            })) != null) {

                throw new IllegalStateException("overwrite element handler by method " + method);
            }
        }
    }

    private List<Runnable> getRunnableHandlers(Method[] methods, Class<? extends Annotation> annotationClass) {

        List<Runnable> result = new ArrayList<>();

        for (Method method : methods) {

            if (method.getAnnotation(annotationClass) == null) {

                continue;
            }

            if (method.getParameters().length != 0) {

                continue;
            }

            result.add(() -> {
                try {

                    method.invoke(this);

                } catch (IllegalAccessException e) {

                    throw new RuntimeException(e);

                } catch (InvocationTargetException e) {

                    if (e.getCause() instanceof RuntimeException) {

                        throw (RuntimeException) e.getCause();
                    }

                    throw new RuntimeException(e);
                }
            });
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void addAnnotationClass(Class<?> annotationClass) {

        annotationClassesByName.put(annotationClass.getCanonicalName(), (Class<? extends Annotation>) annotationClass);
    }
}
