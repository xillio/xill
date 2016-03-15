package nl.xillio.xill.components.expressions.pipeline;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.WrappingIterator;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;

/**
 * This class represents the implementation of the consume function. This function will iterate an iterable argument and
 * return the number processed items.
 *
 * @author Thomas Biesaart
 * @author Andrea Parrilli
 */
public class ConsumeTerminalExpression extends AbstractPipelineTerminalExpression {

    public ConsumeTerminalExpression(Processable input) {
        super(input);
    }

    @Override
    protected MetaExpression reduce(MetaExpression inputValue, Debugger debugger) {
        long count = process(inputValue);

        return fromValue(count);
    }

    private long process(MetaExpression inputValue) {
        switch (inputValue.getType()) {
            case ATOMIC:
                return count(inputValue);
            default:
                return inputValue.getSize().longValue();
        }
    }

    private long count(MetaExpression inputValue) {
        long result = 0;
        try (WrappingIterator iterator = iterate(inputValue)) {
            while (iterator.hasNext()) {
                MetaExpression expression = iterator.next();
                if (!expression.isDisposalPrevented()) {
                    expression.registerReference();
                    expression.releaseReference();
                }
                result++;
            }
        }
        return result;
    }
}
