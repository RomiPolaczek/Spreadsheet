package expression.parser;

import expression.parser.Operation;
import java.util.List;

public class FunctionArgument {

    private final Operation operation;
    private final List<FunctionArgument> nestedArguments;
    private final String value;
    private final boolean isFunction;

    public FunctionArgument(Operation operation, List<FunctionArgument> nestedArguments) {
        this.operation = operation;
        this.nestedArguments = nestedArguments;
        this.value = null;
        this.isFunction = true;
    }

    public FunctionArgument(String value) {
        this.operation = null;
        this.nestedArguments = null;
        this.value = value;
        this.isFunction = false;
    }

    public boolean isFunction() {
        return isFunction;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<FunctionArgument> getNestedArguments() {
        return nestedArguments;
    }

    public String getValue() {
        return value;
    }
}



