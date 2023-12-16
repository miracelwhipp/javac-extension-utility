package io.github.miracelwhipp.javac.extension.configuration;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandLine {

    private final Map<String, List<String>> parameters;
    private final List<String> arguments;

    private final List<String> source;

    public CommandLine(Map<String, List<String>> parameters, List<String> arguments, List<String> source) {
        this.parameters = parameters;
        this.arguments = arguments;
        this.source = source;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public List<String> getSource() {
        return source;
    }

    public static CommandLine parse(String... args) {

        return parse(Arrays.asList(args));
    }

    public static CommandLine parse(List<String> args) {

        Map<String, List<String>> parameters = new LinkedHashMap<>();

        List<String> arguments = new ArrayList<>();

        Queue<String> argumentsLeft = new ArrayDeque<>(args);

        while (!argumentsLeft.isEmpty()) {

            String argument = argumentsLeft.poll();

            if (argument.startsWith("-")) {

                String parameterName;
                String parameterValue;

                if (argument.contains("=")) {

                    String[] split = argument.split("=", 2);

                    parameterName = parseParameterName(split[0]);
                    parameterValue = split[1];

                } else {

                    if (argumentsLeft.isEmpty()) {

                        throw new IllegalArgumentException("argument " + argument + " is specified like a named parameter, but no value is given");
                    }

                    parameterName = parseParameterName(argument);
                    parameterValue = argumentsLeft.poll();
                }

                parameters.computeIfAbsent(parameterName, key -> new ArrayList<>()).add(parameterValue);

            } else {

                arguments.add(argument);
            }
        }

        return new CommandLine(parameters, arguments, args);
    }

    private static String parseParameterName(String argument) {

        return argument.replaceAll("-", "");
    }

    public <Type> Configurator<Type> configuratorForClass(Class<? extends Type> clazz) {

        CommandLine me = this;

        List<BiConsumer<Type, CommandLine>> setters = new ArrayList<>();

        findSettersForClass(clazz, setters, new int[]{0});

        return new Configurator<>(
                setters.stream()
                        .map((BiConsumer<Type, CommandLine> setter) -> ((Consumer<Type>) type -> setter.accept(type, me)))
                        .collect(Collectors.toList())
        );
    }

    private static <Type> void findSettersForClass(Class<?> clazz, List<BiConsumer<Type, CommandLine>> setters, int[] position) {

        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null && superclass != Object.class) {

            findSettersForClass(superclass, setters, position);
        }

        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {

            Parameter parameter = field.getAnnotation(Parameter.class);
            Argument argument = field.getAnnotation(Argument.class);

            if (parameter == null && argument == null) {

                continue;
            }

            if (parameter != null) {

                CommandLineElementParser<?> commandLineElementParser = ConfigurationPropertyParsers.fromField(field, Arrays.asList(parameter.defaultValue()));

                if (commandLineElementParser == null) {

                    throw new IllegalArgumentException("cannot specify field " + field.getDeclaringClass().getCanonicalName() + "." + field.getName() + " as parameter, no parser for type " + field.getType() + " defined");
                }

                field.setAccessible(true);

                setters.add((element, commandLine) -> {

                    try {

                        field.set(element, commandLineElementParser.parse(commandLine.getParameters().get(parameter.name())));

                    } catch (IllegalAccessException e) {

                        throw new RuntimeException(e);
                    }
                });

                continue;
            }

            int argumentPosition = position[0]++;
            CommandLineElementParser<?> commandLineElementParser = ConfigurationPropertyParsers.fromField(field, Arrays.asList(argument.defaultValue()));
            field.setAccessible(true);

            setters.add((element, commandLine) -> {

                try {

                    field.set(element, commandLineElementParser.parse(List.of(commandLine.getArguments().get(argumentPosition))));

                } catch (IllegalAccessException e) {

                    throw new RuntimeException(e);
                }
            });
        }


    }

}
