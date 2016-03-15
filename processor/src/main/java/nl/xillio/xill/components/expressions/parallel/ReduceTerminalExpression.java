package nl.xillio.xill.components.expressions.parallel;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.WrappingIterator;
import nl.xillio.xill.components.expressions.FunctionParameterExpression;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.Arrays;

/**
 * This class represents the implementation of the reduce function. This function takes an iterable and an accumulator
 * so it can produce a summary of the iterator.
 *
 * @author Thomas Biesaart
 * @author Andrea Parrilli
 */
public class ReduceTerminalExpression extends AbstractPipelineTerminalExpression implements FunctionParameterExpression {
    private final Processable accumulator;
    private FunctionDeclaration function;

    public ReduceTerminalExpression(Processable accumulator, Processable input) {
        super(input);
        this.accumulator = accumulator;
    }

    @Override
    protected MetaExpression reduce(MetaExpression inputValue, Debugger debugger) {
        // Create the iterator
        try (WrappingIterator iterator = iterate(inputValue)) {
            // Get the initial accumulator value
            MetaExpression workingValue = accumulator.process(debugger).get();
            workingValue.registerReference();

            // Walk the iterator
            while (iterator.hasNext()) {
                MetaExpression next = iterator.next();
                workingValue = function.run(debugger, Arrays.asList(workingValue, next)).get();
            }

            // Return the result
            return workingValue;
        }
    }

    @Override
    public void setFunction(FunctionDeclaration function) {
        this.function = function;
    }
}
