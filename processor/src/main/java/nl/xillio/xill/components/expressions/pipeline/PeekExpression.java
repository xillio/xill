package nl.xillio.xill.components.expressions.pipeline;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.WrappingIterator;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.Collections;

/**
 * This class represents the factory for the peek construction. This construction allows you to inspect an iterable without
 * changing it.
 *
 * @author Thomas Biesaart
 */
public class PeekExpression extends PipelineExpression {

    public PeekExpression(Processable input) {
        super(input);
    }

    @Override
    protected WrappingIterator wrap(MetaExpression input, FunctionDeclaration functionDeclaration, Debugger debugger) {
        return new PeekIterator(input, functionDeclaration, debugger);
    }

    @Override
    protected String describe() {
        return "peek";
    }

    /**
     * This class represents the map runtime. It will apply a function to the input to create the next element.
     */
    private class PeekIterator extends WrappingIterator {
        private final FunctionDeclaration function;
        private final Debugger debugger;

        public PeekIterator(MetaExpression host, FunctionDeclaration function, Debugger debugger) {
            super(host);
            this.function = function;
            this.debugger = debugger;
        }

        @Override
        protected MetaExpression transformItem(MetaExpression item) {
            boolean prevented = item.isDisposalPrevented();
            item.preventDisposal();

            InstructionFlow<MetaExpression> result = function.run(debugger, Collections.singletonList(item));

            if (!prevented) {
                item.allowDisposal();
            }

            // Trigger dispose
            if (result.hasValue()) {
                result.get().registerReference();
                result.get().releaseReference();
            }

            return item;
        }
    }
}
