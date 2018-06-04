package analyzer.walker;

import analyzer.event.AnalysisEvent;
import analyzer.visitor.Visitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

@Component
public class AnalysisWalker extends SimpleFileVisitor<Path> {

    private final PathMatcher javaMatcher = FileSystems.getDefault().getPathMatcher("glob:**.java");

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private Collection<Visitor> visitors;

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!javaMatcher.matches(file)) {
            return FileVisitResult.CONTINUE;
        }

        try {
            final CompilationUnit compilationUnit = JavaParser.parse(file);
            visitors.forEach(visitor -> compilationUnit.accept(visitor, null));
        } catch (ParseProblemException e) {
            final AnalysisEvent event = new AnalysisEvent(this, AnalysisEvent.Type.ERROR)
                    .setClassName(file.toString())
                    .setMessage("unable to parse file because of error: " + e.getMessage());
            publisher.publishEvent(event);
        }

        return FileVisitResult.CONTINUE;
    }
}
