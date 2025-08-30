package lk.jiat.globemed.interpreter;

public interface Expression {

    boolean interpret(QueryContext context);

    String toString();
}
