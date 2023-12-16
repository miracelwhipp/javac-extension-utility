package io.github.miracelwhipp.javac.extension.configuration;

import javax.annotation.processing.ProcessingEnvironment;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ConfigurationPropertyParsers {

    private ConfigurationPropertyParsers() {
    }

    @FunctionalInterface
    public interface PropertyParser<Type> {

        Type parse(Class<Type> clazz, String text);
    }

    private static Map<Class<?>, PropertyParser<?>> SINGLE_ELEMENT_PARSERS = new HashMap<>();

    static {

        SINGLE_ELEMENT_PARSERS.put(String.class, (clazz, text) -> text);
        SINGLE_ELEMENT_PARSERS.put(Double.class, (clazz, text) -> Double.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(double.class, (clazz, text) -> Double.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(Float.class, (clazz, text) -> Float.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(float.class, (clazz, text) -> Float.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(Long.class, (clazz, text) -> Long.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(long.class, (clazz, text) -> Long.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(Integer.class, (clazz, text) -> Integer.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(int.class, (clazz, text) -> Integer.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(Short.class, (clazz, text) -> Short.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(short.class, (clazz, text) -> Short.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(Byte.class, (clazz, text) -> Byte.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(byte.class, (clazz, text) -> Byte.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(Boolean.class, (clazz, text) -> Boolean.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(boolean.class, (clazz, text) -> Boolean.valueOf(text));
        SINGLE_ELEMENT_PARSERS.put(Character.class, (clazz, text) -> ConfigurationPropertyParsers.parseCharacter(text));
        SINGLE_ELEMENT_PARSERS.put(char.class, (clazz, text) -> ConfigurationPropertyParsers.parseCharacter(text));
        SINGLE_ELEMENT_PARSERS.put(Enum.class, (clazz, text) -> Enum.valueOf(cast(clazz), text));

        var parsers = ServiceLoader.load(PropertyParser.class);

        parsers.forEach(propertyParser -> {

            Type genericInterface = getGenericInterface(cast(PropertyParser.class), propertyParser.getClass());

            if (!(genericInterface instanceof ParameterizedType)) {

                return;
            }

            Type typeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];

            if (!(typeArgument instanceof Class<?>)) {

                return;
            }

            SINGLE_ELEMENT_PARSERS.put(cast(typeArgument), propertyParser);
        });
    }

    private static Type getGenericInterface(Class<?> interfaceSearched, Class<?> clazz) {

        Optional<Type> result = Arrays.stream(clazz.getGenericInterfaces())
                .filter(interfaze -> interfaze.getTypeName().equals(interfaceSearched.getCanonicalName())).findFirst();

        if (result.isPresent()) {

            return result.get();
        }

        Class<?> superclass = clazz.getSuperclass();

        if (superclass == null || superclass == Object.class) {

            return null;
        }

        return getGenericInterface(interfaceSearched, superclass);
    }

    private static <Type> CommandLineElementParser<Type> toCommandLineParser(PropertyParser<Type> propertyParser, Class<Type> clazz) {

        if (propertyParser == null) {

            return null;
        }

        return (List<String> elements) -> propertyParser.parse(clazz, elementize(elements));
    }

    private static String elementize(List<String> elements) {

        if (elements == null) {

            return null;
        }

        if (elements.size() > 1) {

            throw new IllegalArgumentException(); // TODO: specify exception or at least message
        }

        if (elements.isEmpty()) {

            return null;
        }

        return elements.get(0);
    }


    @SuppressWarnings("unchecked")
    public static <Type> Type cast(Object instance) {

        return (Type) instance;
    }

    private static char parseCharacter(String string) {

        if (string.length() != 1) {

            throw new IllegalStateException("cannot parse " + string + " as character");
        }

        return string.charAt(0);
    }

    public static CommandLineElementParser<?> fromField(Field field, List<String> defaultValue) {

        Class<?> type = field.getType();

        if (!Collection.class.isAssignableFrom(type)) {

            return toCommandLineParser(cast(elementParser(type, elementize(defaultValue))), cast(type));
        }

        if (type.isAssignableFrom(Set.class)) {

            return collectionParser(defaultValue, type, Set.class, HashSet::new);
        }

        return collectionParser(defaultValue, type, List.class, ArrayList::new);
    }

    private static <CollectionType extends Collection<?>> CommandLineElementParser<Object> collectionParser(List<String> defaultValue, Class<?> type, Class<CollectionType> collectionClass, Supplier<CollectionType> collectionFactory) {

        Type genericSet = getGenericInterface(collectionClass, type);

        if (!(genericSet instanceof ParameterizedType)) {

            return null;
        }

        Type elemenType = ((ParameterizedType) genericSet).getActualTypeArguments()[0];

        if (!(elemenType instanceof Class<?>)) {

            return null;
        }

        Class<?> elementClass = (Class<?>) elemenType;

        PropertyParser<?> propertyParser = SINGLE_ELEMENT_PARSERS.get(elementClass);

        if (propertyParser == null) {

            return null;
        }

        return strings -> {

            CollectionType result = collectionFactory.get();

            if (strings == null) {

                strings = defaultValue;
            }

            for (String string : strings) {

                result.add(cast(propertyParser.parse(cast(elementClass), string)));
            }

            return result;
        };
    }

    private static PropertyParser<?> elementParser(Class<?> type, String defaultValue) {

        PropertyParser<?> result = SINGLE_ELEMENT_PARSERS.get(type);

        if (result == null) {

            return null;
        }

        return (clazz, text) -> {

            if (text == null) {

                text = defaultValue;
            }

            return result.parse(cast(clazz), text);
        };
    }

    public static Map<String, Consumer<ProcessingEnvironment>> parameterSettersForInstance(Object instance) {

        LinkedHashMap<String, Consumer<ProcessingEnvironment>> result = new LinkedHashMap<>();

        collectParameterSettersForClass(instance.getClass(), instance, result);

        return result;
    }

    private static void collectParameterSettersForClass(Class<?> clazz, Object instance, LinkedHashMap<String, Consumer<ProcessingEnvironment>> result) {

        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null && superclass != Object.class) {

            collectParameterSettersForClass(superclass, instance, result);
        }

        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {

            Parameter parameterAnnotation = field.getAnnotation(Parameter.class);

            if (parameterAnnotation == null) {

                continue;
            }

            String[] defaultValues = parameterAnnotation.defaultValue();
            String defaultValue;

            if (defaultValues == null || defaultValues.length == 0) {

                defaultValue = null;

            } else {

                defaultValue = defaultValues[0];
            }

            Class<?> type = field.getType();
            field.setAccessible(true);

            PropertyParser<?> propertyParser = SINGLE_ELEMENT_PARSERS.get(type);

            if (propertyParser == null) {

                continue;
            }

            result.put(parameterAnnotation.name(), environment -> {

                try {

                    field.set(instance, propertyParser.parse(cast(type), environment.getOptions().getOrDefault(parameterAnnotation.name(), defaultValue)));

                } catch (IllegalAccessException e) {

                    throw new IllegalStateException(e);
                }
            });
        }
    }


}
