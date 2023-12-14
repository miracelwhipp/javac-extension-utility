package io.github.miracelwhipp.compiler.plugin.test;


import com.google.auto.service.AutoService;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import io.github.miracelwhipp.javac.extension.compiler.plugin.*;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@PluginName("simple-compiler-plugin")
@JavaCompilerTaskListener(SimpleCompilerPlugin.StartTaskListener.class)
@JavaCompilerTaskListener(SimpleCompilerPlugin.FinishTaskListener.class)
@AutoService(Plugin.class)
public class SimpleCompilerPlugin extends ReflectiveCompilerPlugin {

    public static class AbstractListener extends ReflectiveJavaCompilerTaskListener {

        @Option(name = "-o", aliases = {"--output-file"}, usage = "the file to log to")
        protected String outputFile = "target/log.txt";

        protected void log(String message) {

            try {

                File file = new File(outputFile);
                file.getParentFile().mkdirs();

                Files.writeString(file.toPath(), message + "\n", StandardOpenOption.WRITE, file.exists() ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);

            } catch (IOException e) {

                throw new IllegalStateException(e);
            }
        }
    }

    public static class StartTaskListener extends AbstractListener {

        void log(TaskEvent event) {

            log("on " + event.getKind().toString());
        }

        @StartJavaCompilerEvent(TaskEvent.Kind.PARSE)
        public void parse(TaskEvent event) {

            log(event);
        }

        @StartJavaCompilerEvent(TaskEvent.Kind.ENTER)
        public void enter(TaskEvent event) {

            log(event);
        }

        @StartJavaCompilerEvent(TaskEvent.Kind.ANALYZE)
        public void analyze(TaskEvent event) {

            log(event);
        }

        @StartJavaCompilerEvent(TaskEvent.Kind.GENERATE)
        public void generate(TaskEvent event) {

            log(event);
        }

        @StartJavaCompilerEvent(TaskEvent.Kind.ANNOTATION_PROCESSING)
        public void process(TaskEvent event) {

            log(event);
        }

        @StartJavaCompilerEvent(TaskEvent.Kind.ANNOTATION_PROCESSING_ROUND)
        public void processRound(TaskEvent event) {

            log(event);
        }

        @StartJavaCompilerEvent(TaskEvent.Kind.COMPILATION)
        public void compilation(TaskEvent event) {

            log(event);
        }
    }

    public static class FinishTaskListener extends AbstractListener {

        void log(TaskEvent event) {

            log("after " + event.getKind().toString());
        }

        @AfterJavaCompilerEvent(TaskEvent.Kind.PARSE)
        public void parse(TaskEvent event) {

            log(event);
        }

        @AfterJavaCompilerEvent(TaskEvent.Kind.ENTER)
        public void enter(TaskEvent event) {

            log(event);
        }

        @AfterJavaCompilerEvent(TaskEvent.Kind.ANALYZE)
        public void analyze(TaskEvent event) {

            event.getCompilationUnit().accept(new AssignmentCounter(), outputFile);

            log(event);
        }

        @AfterJavaCompilerEvent(TaskEvent.Kind.GENERATE)
        public void generate(TaskEvent event) {

            log(event);
        }

        @AfterJavaCompilerEvent(TaskEvent.Kind.ANNOTATION_PROCESSING)
        public void process(TaskEvent event) {

            log(event);
        }

        @AfterJavaCompilerEvent(TaskEvent.Kind.ANNOTATION_PROCESSING_ROUND)
        public void processRound(TaskEvent event) {

            log(event);
        }

        @AfterJavaCompilerEvent(TaskEvent.Kind.COMPILATION)
        public void compilation(TaskEvent event) {

            log(event);
        }
    }
}