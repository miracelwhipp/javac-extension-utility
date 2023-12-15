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

// tag::header[]
public class SimpleAnnotationProcessor extends ReflectiveAnnotationProcessor { // <1>
// end::header[]

    @Parameter(name = "annotation.count.factor", defaultValue = "1")
    private int factor;

    private int currentSum = 0;

    private StringBuilder target = new StringBuilder();


// tag::method-handler-header[]
    @ElementHandler // <2>
    public void countMethods(ToCount annotation, ExecutableElement element) {

// end::method-handler-header[]
        target.append("adding sum\n");

        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "adding sum ", element);

        currentSum += factor;
    }

// tag::field-handler-header[]
    @ElementHandler // <3>
    public void countFields(ToCount annotation, VariableElement element) {

// end::field-handler-header[]
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