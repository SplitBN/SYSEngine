package dev.splityosis.sysengine.function;

import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;

/**
 * Builds a Function by reading lines that can contain domain conditions
 * and an expression. Lines that have no colon (":") are treated as
 * "unconditional" expressions to be used if reached.
 * <p>
 * Example line inputs:
 * <p>
 * {@code - "level < 10: 40*level-30"} <p>
 * {@code - "level >= 10 && level < 20: 8*level+577"} <p>
 * {@code - "6180*level-92600"  (no condition; always used if reached)} <p>
 *
 */
public class FunctionBuilder {

    private final List<String> rawLines = new ArrayList<>();
    private final Set<String> variableNames = new LinkedHashSet<>();

    public FunctionBuilder() {}

    public FunctionBuilder(List<String> lines) {
        this.rawLines.addAll(lines);
    }

    public FunctionBuilder(String... lines) {
        this(Arrays.asList(lines));
    }

    /**
     * Add recognized variable names that can appear in the expressions or conditions.
     *
     * @param vars The variable names (e.g. "level", "x", "y", etc.).
     * @return This builder for chaining.
     */
    public FunctionBuilder variables(String... vars) {
        Collections.addAll(variableNames, vars);
        return this;
    }

    /**
     * Adds a line that represents either:
     *  - conditions separated by "&&" then ":" then an expression
     *  - OR a default expression with no colon.
     *
     * @param line The raw line to parse later.
     * @return This builder for chaining.
     */
    public FunctionBuilder addLine(String line) {
        rawLines.add(line.trim());
        return this;
    }

    public FunctionBuilder addLines(String... lines) {
        for (String line : lines)
            addLine(line);
        return this;
    }

    /**
     * Builds and returns a Function based on all added lines.
     */
    public Function build() {
        List<DomainDefinition> domains = new ArrayList<>();

        for (String line : rawLines) {
            String[] split = line.split(":");
            if (split.length == 1) {
                // Unconditional domain
                domains.add(new DomainDefinition(
                        new ArrayList<>(), // no conditions
                        new ExpressionBuilder(split[0])
                                .variables(variableNames)
                                .build()
                ));
            } else {
                String conditionPart = split[0].trim();
                String expressionPart = split[1].trim();

                List<Condition> conditions = new ArrayList<>();
                // Split multiple conditions by "&&"
                String[] conditionTokens = conditionPart.split("&&");
                for (String conditionToken : conditionTokens) {
                    Condition condition = Condition.parse(conditionToken.trim(), variableNames);
                    conditions.add(condition);
                }

                domains.add(new DomainDefinition(
                        conditions,
                        new ExpressionBuilder(expressionPart)
                                .variables(variableNames)
                                .build()
                ));
            }
        }

        return new DomainFunction(rawLines, domains, variableNames);
    }

    public List<String> getRawLines() {
        return rawLines;
    }

    public Set<String> getVariableNames() {
        return variableNames;
    }
}