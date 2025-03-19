package dev.splityosis.sysengine.function;

/**
 * Enumerates the possible operators for conditions like <, <=, >, >=, ==.
 */
public enum ConditionOperator {
    LESS("<") {
        @Override
        public boolean compare(double left, double right) {
            return left < right;
        }
    },
    LESS_EQUAL("<=") {
        @Override
        public boolean compare(double left, double right) {
            return left <= right;
        }
    },
    GREATER(">") {
        @Override
        public boolean compare(double left, double right) {
            return left > right;
        }
    },
    GREATER_EQUAL(">=") {
        @Override
        public boolean compare(double left, double right) {
            return left >= right;
        }
    },
    EQUAL("==") {
        @Override
        public boolean compare(double left, double right) {
            return Double.compare(left, right) == 0;
        }
    },

    NOT_EQUAL("!=") {
        @Override
        public boolean compare(double left, double right) {
            return Double.compare(left, right) != 0;
        }
    };

    private final String symbol;

    ConditionOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public abstract boolean compare(double left, double right);

    public static ConditionOperator fromSymbol(String symbol) {
        for (ConditionOperator op : values()) {
            if (op.getSymbol().equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operator symbol: " + symbol);
    }
}
