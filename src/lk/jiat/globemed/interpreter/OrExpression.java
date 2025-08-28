package lk.jiat.globemed.interpreter;

/**
 * Non-terminal Expression for Interpreter Pattern
 * Implements logical OR operation
 */
public class OrExpression implements Expression {
    private Expression expr1;
    private Expression expr2;
    
    public OrExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public boolean interpret(QueryContext context) {
        return expr1.interpret(context) || expr2.interpret(context);
    }
    
    @Override
    public String toString() {
        return String.format("(%s OR %s)", expr1.toString(), expr2.toString());
    }
}