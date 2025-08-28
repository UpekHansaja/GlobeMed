package lk.jiat.globemed.interpreter;

/**
 * Abstract Expression interface for Interpreter Pattern
 * Defines the interpret operation for query expressions
 */
public interface Expression {
    boolean interpret(QueryContext context);
    String toString();
}