package analyzer.visitor;

import analyzer.event.AnalysisEvent;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.springframework.stereotype.Component;

@Component
public class CatchExceptionIgnoreVisitor extends Visitor {

    @Override
    public void visit(CatchClause n, Void arg) {
        final Parameter exception = n.getParameter();

        if ("ignored".equals(exception.getNameAsString())) {
            return;
        }

        boolean isFine = n.getBody().getStatements().stream().anyMatch(Statement::isThrowStmt);

        isFine |= n.getBody().getStatements().stream()
                .filter(Statement::isExpressionStmt)
                .map(Statement::asExpressionStmt)
                .map(ExpressionStmt::getExpression)
                .filter(Expression::isMethodCallExpr)
                .map(Expression::asMethodCallExpr)
                .flatMap(call -> call.getArguments().stream())
                .filter(Expression::isNameExpr)
                .map(Expression::asNameExpr)
                .anyMatch(name -> name.getName().equals(exception.getName()));

        if (!isFine) {
            event(
                    AnalysisEvent.Type.WARNING,
                    "ignoring exception " + quoted(exception.getNameAsString()),
                    exception.getRange().orElse(null));
        }
    }
}
