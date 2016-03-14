package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.WrappingIterator;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * This class represents the factory for the filter construction. This construction allows you to remove certain elements
 * from an iterable.
 *
 * @author Thomas Biesaart
 */
public class FilterExpression extends PipelineExpression {

    public FilterExpression(Processable input) {
        super(input);
    }

    @Override
    protected WrappingIterator wrap(MetaExpression input, FunctionDeclaration functionDeclaration, Debugger debugger) {
        return new FilterIterator(input, functionDeclaration, debugger);
    }

    @Override
    protected String describe() {
        return "filter";
    }

    /**
     * This class represents the implementation of the filter runtime.
     * It will cache 1 item continuously to check if it matches the predicate.
     */
    private class FilterIterator extends WrappingIterator {
        private final FunctionDeclaration function;
        private final Debugger debugger;
        private MetaExpression next;

        public FilterIterator(MetaExpression host, FunctionDeclaration function, Debugger debugger) {
            super(host);
            this.function = function;
            this.debugger = debugger;
        }

        @Override
        public boolean hasNext() {
            cacheNext();
            return next != null;
        }

        private void cacheNext() {
            while (next == null && super.hasNext()) {
                MetaExpression value = super.next();
                value.registerReference();
                MetaExpression shouldKeep = function.run(debugger, Collections.singletonList(value)).get();
                if (shouldKeep.getBooleanValue()) {
                    // We are done with this but since we are returning it we don't want to dispose
                    value.preventDisposal();
                    value.releaseReference();
                    value.allowDisposal();

                    next = value;
                } else {
                    // We are done with this
                    value.releaseReference();
                }
            }
        }

        @Override
        public MetaExpression next() {
            if (!hasNext()) {
                throw new NoSuchElementException("This iterator is empty");
            }
            MetaExpression result = next;
            next = null;
            return result;
        }

        @Override
        protected MetaExpression transformItem(MetaExpression item) {
            return item;
        }
    }
}
