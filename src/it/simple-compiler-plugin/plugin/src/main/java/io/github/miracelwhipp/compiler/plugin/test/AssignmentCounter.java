package io.github.miracelwhipp.compiler.plugin.test;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class AssignmentCounter extends TreeScanner<Void, String> {

    @Override
    public Void visitAssignment(AssignmentTree node, String outputFile) {

        try {

            Files.writeString(new File(outputFile).toPath(), "found assignment " + node.toString() + "\n", StandardOpenOption.APPEND);

        } catch (IOException e) {

            throw new IllegalStateException(e);
        }

        return super.visitAssignment(node, outputFile);
    }


    @Override
    public Void visitVariable(VariableTree node, String outputFile) {

        if (node.getInitializer() != null) {

            try {

                Files.writeString(new File(outputFile).toPath(), "found assignment " + node.toString() + "\n", StandardOpenOption.APPEND);

            } catch (IOException e) {

                throw new IllegalStateException(e);
            }

        }

        return super.visitVariable(node, outputFile);
    }
}
