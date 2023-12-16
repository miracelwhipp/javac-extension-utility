package io.github.miracelwhipp.javac.extension.configuration;

import java.util.List;

@FunctionalInterface
public interface CommandLineElementParser<Type> {

    Type parse(List<String> elements);
}
