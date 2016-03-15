package nl.xillio.xill.components.expressions.pipeline;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.WrappingIterator;

import java.util.Collection;
import java.util.Collections;

/**
 * This class represent the base implementation of a function that will reduce an iterable input to a summary.
 *
 * @author Thomas Biesaart
 * @author Andrea Parrilli
 */
public abstract class AbstractPipelineTerminalExpression implements Processable {
    private final Processable input;

    public AbstractPipelineTerminalExpression(Processable input) {
        this.input = input;
    }

    @Override
    public InstructionFlow<MetaExpression> process(Debugger debugger) {
        MetaExpression iterableValue = input.process(debugger).get();

        return InstructionFlow.doResume(reduce(iterableValue, debugger));
    }

    protected WrappingIterator iterate(MetaExpression expression) {
        return WrappingIterator.identity(expression);
    }

    protected abstract MetaExpression reduce(MetaExpression inputValue, Debugger debugger);

    @Override
    public Collection<Processable> getChildren() {
        return Collections.singletonList(input);
    }
}
