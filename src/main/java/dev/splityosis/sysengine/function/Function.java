package dev.splityosis.sysengine.function;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Function interface that takes a map of variable values
 * and returns a double as the evaluated result.
 */
public interface Function {

    /**
     * Evaluates the function using the provided variables.
     *
     * @param variables A map of variable names to their values.
     * @return The computed double result.
     */
    double evaluate(Map<String, Double> variables);

    /**
     * Takes in varargs in the form of {key1, value1, key2, value2, ...},
     * constructs a variables map and calls {@link #evaluate(Map)}
     * <p>
     * Usage example:
     * <pre>
     * {@code
     *   function.evaluate("level", 15, "hp", 100.0);
     *   }
     * </pre>
     *
     * @param varValPairs
     * @return
     */
    default double evaluate(Object... varValPairs) {
        if (varValPairs == null)
            return evaluate(new HashMap<>());

        if (varValPairs.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "The varargs must come in pairs: (String varName, Number value)."
            );
        }

        Map<String, Double> variables = new HashMap<>();

        for (int i = 0; i < varValPairs.length; i += 2) {
            Object key = varValPairs[i];
            Object val = varValPairs[i + 1];

            if (!(key instanceof String))
                throw new IllegalArgumentException("Expected String for variable name at index " + i + " but got: " + key);

            if (!(val instanceof Number))
                throw new IllegalArgumentException("Expected Number for variable value at index " + (i + 1) + " but got: " + val);

            String varName = (String) key;
            double doubleVal = ((Number) val).doubleValue();
            variables.put(varName, doubleVal);
        }

        return evaluate(variables);
    }

    /**
     * Returns the names of variables recognised by this function.
     */
    Collection<String> getVariableNames();

    /**
     * Returns the expression lines of this function
     */
    Collection<String> getLines();

    static FunctionBuilder builder() {
        return new FunctionBuilder();
    }

    static FunctionBuilder builder(List<String> lines) {
        return new FunctionBuilder(lines);
    }

    static FunctionBuilder builder(String... lines) {
        return new FunctionBuilder(lines);
    }
}