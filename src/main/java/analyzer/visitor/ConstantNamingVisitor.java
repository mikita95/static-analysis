package analyzer.visitor;

import analyzer.event.AnalysisEvent;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.springframework.stereotype.Component;

@Component
public class ConstantNamingVisitor extends Visitor {

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        if (n.isFinal() && n.isStatic()) {
            n.getVariables().forEach(v -> {
                final var name = v.getNameAsString();
                if (!name.equals(name.toUpperCase())) {
                    event(
                            AnalysisEvent.Type.INFO,
                            String.format("constant field %s should use uppercase letters", quoted(name)),
                            v.getRange().orElse(null)
                    );
                }
            });
        }
    }
}
