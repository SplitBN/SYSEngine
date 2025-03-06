package dev.splityosis.sysengine.function;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.Expression;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a single condition like "level < 10" or "level >= 20".
 */
public class Condition {

    private static final Pattern OPERATOR_PATTERN = Pattern.compile("([<>]=?|==)");

    private final String variableName;
    private final ConditionOperator conditionOperator;
    private final Expression rightExpression;

    /**
     * Constructs a Condition from fully parsed components.
     */
    public Condition(String variableName, ConditionOperator conditionOperator, Expression rightExpression) {
        this.variableName = variableName;
        this.conditionOperator = conditionOperator;
        this.rightExpression = rightExpression;
    }

    /**
     * Evaluates whether this condition is true for the given variable values.
     */
    public boolean isTrue(Map<String, Double> variables) {
        // Left side is the variable value
        Double leftVal = variables.get(variableName);
        if (leftVal == null) {
            throw new IllegalArgumentException(
                    "Variable '" + variableName + "' was not provided in the variables map."
            );
        }

        // Evaluate the right side
        double rightVal = rightExpression
                .setVariables(variables)
                .evaluate();

        return conditionOperator.compare(leftVal, rightVal);
    }

    /**
     * Parse a string like "level < 10" or "level >= 20" into a Condition.
     */
    public static Condition parse(String raw, Set<String> validVariables) {
        Matcher matcher = OPERATOR_PATTERN.matcher(raw);
        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Condition '" + raw + "' does not contain a valid operator."
            );
        }

        String opSymbol = matcher.group(1);
        ConditionOperator conditionOperator = ConditionOperator.fromSymbol(opSymbol);

        String left = raw.substring(0, matcher.start()).trim();
        String right = raw.substring(matcher.end()).trim();

        if (!validVariables.contains(left)) {
            throw new IllegalArgumentException(
                    "Variable '" + left + "' not in the set of known variables: " + validVariables
            );
        }

        Expression rightExpression = new ExpressionBuilder(right)
                .variables(validVariables)
                .build();

        return new Condition(left, conditionOperator, rightExpression);
    }
}
