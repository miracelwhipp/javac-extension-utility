package io.github.miracelwhipp.javac.extension.compiler.plugin;

/**
 * This class implements the core java compiler task listener invocation. To use it define a subclass of it. More
 * documentation can be found
 * <a href="https://miracelwhipp.github.io/javac-extension-utility/#_writing_compiler_plugins">here</a>.
 */
public abstract class ReflectiveJavaCompilerTaskListener extends RegistryBasedTaskListener {

    protected ReflectiveJavaCompilerTaskListener() {
        super();
        registry = CompilerTaskEventListenerRegistry.forInstance(this);
    }

}
