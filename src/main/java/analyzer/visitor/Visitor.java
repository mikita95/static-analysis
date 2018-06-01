package analyzer.visitor;

import analyzer.event.AnalysisEvent;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public abstract class Visitor extends VoidVisitorAdapter<Void> implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;
    private String className;

    @Override
    public void visit(CompilationUnit n, Void arg) {
        this.className = n.getTypes().stream()
                .map(TypeDeclaration::getNameAsString)
                .findFirst()
                .orElse(null);
        super.visit(n, arg);
    }

    protected void log(final AnalysisEvent.Type type, @Nullable final String message) {
        final var event = new AnalysisEvent(this, type)
                .setMessage(message)
                .setClassName(className);
        publisher.publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    protected String range(@NonNull final Node node) {
        return node.getRange()
                .map(range -> "[" + range.begin.line + ":" + range.begin.column + "]")
                .orElse("");
    }
}
