package nl.xillio.xill.components.expressions.parallel;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.components.expressions.FunctionParameterExpression;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.Collections;

/**
 * This class represents the implementation of the foreach function. This function will apply a function to every element
 * in an iterable argument and return nothing.
 *
 * @author Thomas Biesaart
 * @author Andrea Parrilli
 */
public class ForeachTerminalExpression extends AbstractPipelineTerminalExpression implements FunctionParameterExpression {
    private FunctionDeclaration function;

    public ForeachTerminalExpression(Processable input) {
        super(input);
    }

    @Override
    protected MetaExpression reduce(MetaExpression inputValue, Debugger debugger) {
        // Create the iterator
        try (WrappingIterator iterator = iterate(inputValue)) {

            // Walk the iterator
            while (iterator.hasNext()) {
                InstructionFlow<MetaExpression> result = function.run(debugger, Collections.singletonList(iterator.next()));
                if (result.hasValue() && !result.get().isDisposalPrevented()) {
                    result.get().registerReference();
                    result.get().releaseReference();
                }
            }

            // Return the result
            return ExpressionBuilder.NULL;
        }
    }

    @Override
    public void setFunction(FunctionDeclaration function) {
        this.function = function;
    }
}
