package io.github.miracelwhipp.annotation.processor.test;

import io.github.miracelwhipp.javac.extension.annotation.processor.CompletionHandler;
import io.github.miracelwhipp.javac.extension.annotation.processor.ElementHandler;
import io.github.miracelwhipp.javac.extension.annotation.processor.Parameter;
import io.github.miracelwhipp.javac.extension.annotation.processor.ReflectiveAnnotationProcessor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;

public class SimpleAnnotationProcessor extends ReflectiveAnnotationProcessor {

    @Parameter(name = "annotation.count.factor", defaultValue = "1")
    private int factor;

    private int currentSum = 0;

    private StringBuilder target = new StringBuilder();


    @ElementHandler
    public void countMethods(ToCount annotation, ExecutableElement element) {

        target.append("adding sum\n");

        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "adding sum ", element);

        currentSum += factor;
    }

    @ElementHandler
    public void countFields(ToCount annotation, VariableElement element) {

        target.append("adding sum\n");

        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "adding sum ", element);

        currentSum += factor;
    }


    @CompletionHandler
    public void finish() throws IOException {

        target.append("finished. sum is ").append(currentSum).append("\n");

        FileObject resource = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "pack", "build.log");

        try (Writer writer = resource.openWriter()) {

            writer.append(target.toString());

        } catch (IOException e) {

            throw new RuntimeException(e);
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "finished. sum is " + currentSum);
    }


}