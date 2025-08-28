package lk.jiat.globemed.interpreter;

/**
 * Non-terminal Expression for Interpreter Pattern
 * Implements logical AND operation
 */
public class AndExpression implements Expression {
    private Expression expr1;
    private Expression expr2;
    
    public AndExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public boolean interpret(QueryContext context) {
        return expr1.interpret(context) && expr2.interpret(context);
    }
    
    @Override
    public String toString() {
        return String.format("(%s AND %s)", expr1.toString(), expr2.toString());
    }
}