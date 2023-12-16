package io.github.miracelwhipp.javac.extension.configuration;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.List;
import java.util.function.Consumer;

public class Configurator<Type> {

    private final List<Consumer<Type>> setters;

    public Configurator(List<Consumer<Type>> setters) {
        this.setters = setters;
    }

    public void configure(Type element) {

        setters.forEach(setter -> setter.accept(element));
    }
}
