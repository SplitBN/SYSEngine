package dev.splityosis.sysengine.function;

import net.objecthunter.exp4j.Expression;

import java.util.List;

/**
 * Holds a list of conditions ("ranges") and an Expression.
 * If all conditions pass, the expression is valid for that domain.
 */
public class DomainDefinition {

    private final List<Condition> conditions;
    private final Expression expression;

    public DomainDefinition(List<Condition> conditions, Expression expression) {
        this.conditions = conditions;
        this.expression = expression;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public Expression getExpression() {
        return expression;
    }
}
