package io.github.miracelwhipp.annotation.processor.test;

import io.github.miracelwhipp.javac.extension.annotation.processor.ElementHandler;
import io.github.miracelwhipp.javac.extension.annotation.processor.ReflectiveAnnotationProcessor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

public class WarningAnnotationProcessor extends ReflectiveAnnotationProcessor { // <1>

    @ElementHandler // <2>
    public void warnMethod(Warning annotation, ExecutableElement element) {

        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, annotation.value(), element);
    }
    @ElementHandler // <3>
    public void warnField(Warning annotation, VariableElement element) {

        // <4>
        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, annotation.value(), element);
    }
}