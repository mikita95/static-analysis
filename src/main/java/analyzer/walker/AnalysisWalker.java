package analyzer.walker;

import analyzer.event.AnalysisEvent;
import analyzer.visitor.Visitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AnalysisWalker extends SimpleFileVisitor<Path> {

    private final Set<Visitor> visitors = new HashSet<>();
    private final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.java");

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    public void addVisitors(final List<Visitor> beanVisitors) {
        visitors.addAll(beanVisitors);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!matcher.matches(file)) {
            return FileVisitResult.CONTINUE;
        }

        try {
            final var compilationUnit = JavaParser.parse(file);
            visitors.forEach(visitor -> compilationUnit.accept(visitor, null));
        } catch (ParseProblemException e) {
            final var event = new AnalysisEvent(this, AnalysisEvent.Type.ERROR)
                    .setClassName(file.toString())
                    .setMessage("Unable to parse file: " + e.getMessage());
            publisher.publishEvent(event);
        }

        return FileVisitResult.CONTINUE;
    }
}
