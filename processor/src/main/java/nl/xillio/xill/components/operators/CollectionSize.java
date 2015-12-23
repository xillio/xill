package nl.xillio.xill.components.operators;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class is to retrieve the size of a collection (list or object).
 * There is no operand for it since this is for back-end use.
 *
 * @author Pieter Soels.
 */
public final class CollectionSize implements Processable {

    private final Processable collection;

    public CollectionSize(final Processable collection){
        this.collection = collection;
    }

    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        MetaExpression metaCollection = collection.process(debugger).get();

        if (metaCollection.getType() == ExpressionDataType.ATOMIC){
            throw new RobotRuntimeException("You can not retrieve a size of an atomic");
        } else {
            return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(metaCollection.getNumberValue()));
        }
    }

    public Collection<Processable> getChildren() {
        return Arrays.asList(collection);
    }
}
