package io.github.miracelwhipp.annotation.processor.test;

import io.github.miracelwhipp.javac.extension.annotation.processor.ElementHandler;
import io.github.miracelwhipp.javac.extension.annotation.processor.ReflectiveAnnotationProcessor;
import io.github.miracelwhipp.javac.extension.annotation.processor.CompletionHandler;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.Writer;
import java.io.IOException;

public class WarningAnnotationProcessor extends ReflectiveAnnotationProcessor {


    private StringBuilder builder = new StringBuilder(); // <1>

    @ElementHandler
    public void warnMethod(Warning annotation, Element element) { // <2>

        builder.append(annotation.value()).append("\n"); // <3>
    }

    @CompletionHandler
    public void finish() { // <4>

        try {

            FileObject resource = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "warnings.txt");

            try (Writer writer = resource.openWriter()) {

                writer.append(builder.toString());
            }

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}