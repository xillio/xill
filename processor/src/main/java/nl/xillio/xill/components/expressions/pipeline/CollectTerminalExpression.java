package nl.xillio.xill.components.expressions.pipeline;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.WrappingIterator;

import java.util.ArrayList;
import java.util.List;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;

/**
 * This class represents the implementation of the collect function. This function will build a list from all elements
 * in an iterable argument.
 *
 * @author Thomas Biesaart
 * @author Andrea Parrilli
 */
public class CollectTerminalExpression extends AbstractPipelineTerminalExpression {

    public CollectTerminalExpression(Processable input) {
        super(input);
    }

    @Override
    protected MetaExpression reduce(MetaExpression inputValue, Debugger debugger) {
        // Create the iterator
        try (WrappingIterator iterator = iterate(inputValue)) {

            List<MetaExpression> output = new ArrayList<>();
            // Walk the iterator
            while (iterator.hasNext()) {
                output.add(iterator.next());
            }

            return fromValue(output);
        }
    }
}
