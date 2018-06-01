package analyzer.visitor;

import analyzer.event.AnalysisEvent;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UnusedMethodArgumentVisitor extends Visitor {
    private final Set<String> argNames = new HashSet<>();

    @Override
    public void visit(NameExpr anyName, Void arg) {
        argNames.remove(anyName.getNameAsString());
    }

    @Override
    public void visit(MethodDeclaration method, Void arg) {
        final var methodName = method.getNameAsString();
        if ("main".equals(methodName)) {
            return;
        }

        method.getParameters().stream()
                .map(Parameter::getNameAsString)
                .forEach(argNames::add);

        method.getBody().ifPresent(block -> super.visit(block, null));

        argNames.stream()
                .map(p -> String.format("argument %s in method %s is unused.", p, methodName))
                .forEach(message -> log(AnalysisEvent.Type.WARNING, message));
    }
}
