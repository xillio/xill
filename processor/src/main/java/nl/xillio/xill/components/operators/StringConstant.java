package nl.xillio.xill.components.operators;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Collection;
import java.util.Collections;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;

/**
 * This class represents the @ operation.
 */
public final class StringConstant implements Processable {


    private final Processable value;

    public StringConstant(final Processable value) {
        this.value = value;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        MetaExpression resultExpression = value.process(debugger).get();
        resultExpression.registerReference();

        String result = resultExpression.getStringValue();
        resultExpression.releaseReference();

        // Return this value is a constant
        return InstructionFlow.doResume(fromValue(result, true));
    }

    @Override
    public Collection<Processable> getChildren() {
        return Collections.singletonList(value);
    }
}
