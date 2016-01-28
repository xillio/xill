package nl.xillio.xill.components.operators;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is to retrieve the size of a collection (list or object).
 * There is no operand for it since this is for back-end use.
 *
 * @author Pieter Soels.
 */
public final class CollectionSize implements Processable {

    private final Processable collection;

    public CollectionSize(final Processable collection) {
        this.collection = collection;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        MetaExpression metaCollection = collection.process(debugger).get();
        metaCollection.registerReference();

        if (metaCollection.getType() == ExpressionDataType.ATOMIC) {
            metaCollection.releaseReference();
            throw new RobotRuntimeException("You can not retrieve a size of an atomic");
        } else {
            InstructionFlow<MetaExpression> instructionFlow = InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(metaCollection.getNumberValue()));
            metaCollection.releaseReference();
            return instructionFlow;

        }
    }

    @Override
    public Collection<Processable> getChildren() {
        return Collections.singletonList(collection);
    }
}
