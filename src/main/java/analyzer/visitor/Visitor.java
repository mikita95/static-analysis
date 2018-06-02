package analyzer.visitor;

import analyzer.event.AnalysisEvent;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
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

    protected final void event(final AnalysisEvent.Type type,
                               @Nullable final String message,
                               @Nullable final Range range) {
        final var event = new AnalysisEvent(this, type)
                .setMessage(message)
                .setRange(range)
                .setClassName(className);
        publisher.publishEvent(event);
    }

    protected final void event(final AnalysisEvent.Type type, @Nullable final String message) {
        event(type, message, null);
    }

    protected final String quoted(final String s) {
        return "'" + s + "'";
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
