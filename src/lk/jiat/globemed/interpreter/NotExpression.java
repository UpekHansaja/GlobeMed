package lk.jiat.globemed.interpreter;

/**
 * Non-terminal Expression for Interpreter Pattern
 * Implements logical NOT operation
 */
public class NotExpression implements Expression {
    private Expression expression;
    
    public NotExpression(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public boolean interpret(QueryContext context) {
        return !expression.interpret(context);
    }
    
    @Override
    public String toString() {
        return String.format("NOT (%s)", expression.toString());
    }
}