package analyzer.visitor;

import analyzer.event.AnalysisEvent;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.AND;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.OR;
import static com.github.javaparser.ast.expr.UnaryExpr.Operator.LOGICAL_COMPLEMENT;

@Component
public class ConstantBooleanConditionVisitor extends Visitor {

    private Set<Expression> constExpressions = new HashSet<>();

    @Override
    public void visit(IfStmt ifStmt, Void arg) {
        analyze(ifStmt.getCondition());
    }

    @Override
    public void visit(WhileStmt whileStmt, Void arg) {
        analyze(whileStmt.getCondition());
    }

    @Override
    public void visit(DoStmt doStmt, Void arg) {
        analyze(doStmt.getCondition());
    }

    private boolean isConstantValue(final Expression expr) {
        if (expr.isBooleanLiteralExpr()) {
            var value = expr.asBooleanLiteralExpr().getValue();
            event(AnalysisEvent.Type.WARNING,
                    String.format("condition is always " + value, quoted(expr.toString())),
                    expr.getRange().orElse(null)
            );
            if (value) {
                constExpressions.add(expr);
            }
            return true;
        }
        if (constExpressions.stream().anyMatch(trueExpr -> containsEqualWithoutUnary(expr, trueExpr))) {
            event(AnalysisEvent.Type.WARNING,
                    String.format("condition %s is always true", quoted(expr.toString())),
                    expr.getRange().orElse(null)
            );
            constExpressions.add(expr);
            return true;
        }
        if (constExpressions.stream().anyMatch(trueExpr -> containsEqualWithUnary(expr, trueExpr))) {
            event(AnalysisEvent.Type.WARNING,
                    String.format("condition %s is always false", quoted(expr.toString())),
                    expr.getRange().orElse(null)
            );
            return true;
        }
        return false;
    }

    private void handleBinary(final Expression truePart, final Expression analyzePart) {
        constExpressions.add(truePart);
        analyze(analyzePart);
        constExpressions.remove(truePart);
    }

    private void analyze(final Expression expr) {
        if (isConstantValue(expr)) {
            return;
        }
        if (expr.isBinaryExpr()) {
            final var bin = expr.asBinaryExpr();
            analyze(bin.getLeft());
            switch (bin.getOperator()) {
                case AND: {
                    handleBinary(
                            bin.getLeft(),
                            bin.getRight());
                    break;
                }
                case OR: {
                    handleBinary(
                            new UnaryExpr(bin.getLeft(), LOGICAL_COMPLEMENT),
                            bin.getRight());
                    break;
                }
                default: {
                    analyze(bin.getRight());
                    break;
                }
            }
        } else {
            expr.getChildNodes().stream()
                    .filter(node -> node instanceof Expression)
                    .map(node -> (Expression) node)
                    .forEach(this::analyze);
        }

    }

    private boolean containsEqualWithoutUnary(final Expression expr, final Expression currentExpr) {
        if (currentExpr.equals(expr))
            return true;
        if (currentExpr.isEnclosedExpr()) {
            if (containsEqualWithoutUnary(expr, ((EnclosedExpr) currentExpr).getInner()))
                return true;
        }
        if (currentExpr.isBinaryExpr()) {
            final var bin = currentExpr.asBinaryExpr();
            if (bin.getOperator() == AND) {
                return containsEqualWithoutUnary(expr, bin.getLeft()) || containsEqualWithoutUnary(expr, bin.getRight());
            }
        }
        return false;
    }

    private Expression takeLogicalComplement(final Expression expr) {
        if (expr.isBinaryExpr() && expr.asBinaryExpr().getOperator() == AND) {
            return new BinaryExpr(
                    takeLogicalComplement(expr.asBinaryExpr().getLeft()),
                    takeLogicalComplement(expr.asBinaryExpr().getRight()),
                    OR);
        }
        if (expr.isBinaryExpr() && expr.asBinaryExpr().getOperator() == OR) {
            return new BinaryExpr(
                    takeLogicalComplement(expr.asBinaryExpr().getLeft()),
                    takeLogicalComplement(expr.asBinaryExpr().getRight()),
                    AND);
        }
        return new UnaryExpr(
                expr,
                LOGICAL_COMPLEMENT);
    }

    private boolean containsEqualWithUnary(final Expression expr, final Expression currentExpr) {
        if (currentExpr.isUnaryExpr()
                && currentExpr.asUnaryExpr().getOperator() == LOGICAL_COMPLEMENT
                && currentExpr.asUnaryExpr().getExpression().equals(expr))
            return true;
        if (currentExpr.isEnclosedExpr()) {
            if (containsEqualWithUnary(
                    expr,
                    currentExpr.asEnclosedExpr().getInner()))
                return true;
        }
        Expression resultExpression;
        if (currentExpr.isUnaryExpr()
                && currentExpr.asUnaryExpr().getOperator() == LOGICAL_COMPLEMENT)
            resultExpression = takeLogicalComplement(currentExpr.asUnaryExpr().getExpression());
        else
            resultExpression = currentExpr;
        if (resultExpression.isBinaryExpr() && resultExpression.asBinaryExpr().getOperator() == AND) {
            return containsEqualWithUnary(expr, resultExpression.asBinaryExpr().getLeft()) || containsEqualWithUnary(expr, resultExpression.asBinaryExpr().getRight());
        }
        return false;
    }

}
