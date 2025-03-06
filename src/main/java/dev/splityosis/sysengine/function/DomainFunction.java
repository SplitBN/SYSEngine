package dev.splityosis.sysengine.function;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A Function that has multiple DomainDefinitions.
 * Evaluation:
 *   1. Iterate through each domain in the order they were added.
 *   2. Check if all conditions pass.
 *   3. If they do, evaluate that domain's expression and return it.
 *   4. If no domain conditions pass, throw an exception.
 */
public class DomainFunction implements Function {

    private final List<DomainDefinition> domains;
    private final Collection<String> variables;
    private final Collection<String> rawLines;

    public DomainFunction(Collection<String> rawLines, List<DomainDefinition> domains, Collection<String> variables) {
        this.domains = domains;
        this.variables = variables;
        this.rawLines = rawLines;
    }

    @Override
    public double evaluate(Map<String, Double> variables) {
        if (variables == null)
            variables = Collections.emptyMap();

        for (DomainDefinition domain : domains) {
            if (allConditionsPass(domain.getConditions(), variables)) {
                return domain.getExpression()
                        .setVariables(variables)
                        .evaluate();
            }
        }
        // If we reach here, no domain matched
        throw new RuntimeException(
                "No domain conditions satisfied for variables: " + variables
        );
    }

    private boolean allConditionsPass(List<Condition> conditions, Map<String, Double> variables) {
        for (Condition condition : conditions) {
            if (!condition.isTrue(variables)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Collection<String> getVariableNames() {
        return variables;
    }

    @Override
    public Collection<String> getLines() {
        return rawLines;
    }
}
