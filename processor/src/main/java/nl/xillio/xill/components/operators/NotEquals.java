package nl.xillio.xill.components.operators;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Arrays;
import java.util.Collection;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;

/**
 * This class represents the != operator
 */
public class NotEquals implements Processable {

    private final Processable left;
    private final Processable right;

    /**
     * @param left
     * @param right
     */
    public NotEquals(final Processable left, final Processable right) {
        this.left = left;
        this.right = right;

    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        MetaExpression leftValue = left.process(debugger).get();
        leftValue.registerReference();

        MetaExpression rightValue = right.process(debugger).get();
        rightValue.registerReference();

        boolean result = !rightValue.valueEquals(leftValue);

        rightValue.releaseReference();
        leftValue.releaseReference();

        return InstructionFlow.doResume(fromValue(result));
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(left, right);
    }

}
