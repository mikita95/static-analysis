package analyzer.visitor;

import analyzer.event.AnalysisEvent;
import com.github.javaparser.ast.stmt.IfStmt;
import org.springframework.stereotype.Component;

@Component
public class EqualIfBranchesVisitor extends Visitor {

    @Override
    public void visit(IfStmt ifStmt, Void arg) {
        ifStmt.getElseStmt()
                .filter(elseStmt -> ifStmt.getThenStmt().equals(elseStmt))
                .ifPresent(elseStmt -> event(
                        AnalysisEvent.Type.WARNING,
                        "'if' statement has an equal 'then' and 'else' branches.",
                        ifStmt.getRange().orElse(null)));
    }
}
