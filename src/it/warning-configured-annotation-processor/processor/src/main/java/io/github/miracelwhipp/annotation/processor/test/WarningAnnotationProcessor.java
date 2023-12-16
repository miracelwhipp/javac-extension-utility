package io.github.miracelwhipp.annotation.processor.test;

import io.github.miracelwhipp.javac.extension.annotation.processor.ElementHandler;
import io.github.miracelwhipp.javac.extension.annotation.processor.ReflectiveAnnotationProcessor;
import io.github.miracelwhipp.javac.extension.annotation.processor.CompletionHandler;
import io.github.miracelwhipp.javac.extension.configuration.Parameter;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.Writer;
import java.io.IOException;

public class WarningAnnotationProcessor extends ReflectiveAnnotationProcessor {

    @Parameter(name = "target.file", defaultValue = "warnings.txt") // <1>
    private String targetFile;

    private StringBuilder builder = new StringBuilder();

    @ElementHandler
    public void warnMethod(Warning annotation, Element element) {

        builder.append(annotation.value()).append("\n");
    }

    @CompletionHandler
    public void finish() {

        try {

            FileObject resource = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", targetFile); // <2>

            try (Writer writer = resource.openWriter()) {

                writer.append(builder.toString());
            }

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}