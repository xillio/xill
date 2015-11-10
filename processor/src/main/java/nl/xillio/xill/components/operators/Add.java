package nl.xillio.xill.components.operators;

import nl.xillio.util.MathUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This class represents the + operation.
 */
public final class Add extends BinaryNumberOperator {

    public Add(final Processable left, final Processable right) {
        super(left, right, MathUtils::add);
    }

    @SuppressWarnings("unchecked")
    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        MetaExpression leftValue = left.process(debugger).get();
        MetaExpression rightValue = right.process(debugger).get();

        // If both entries are a list, then add them as such
        if (leftValue.getType() == rightValue.getType() && leftValue.getType() == ExpressionDataType.LIST) {
            return InstructionFlow.doResume(processList((List<MetaExpression>) leftValue.getValue(), (List<MetaExpression>) rightValue.getValue(), debugger));
        }

        return super.process(leftValue, rightValue);

    }

    private static MetaExpression processList(final List<MetaExpression> leftValue, final List<MetaExpression> rightValue, final Debugger debugger) throws RobotRuntimeException {
        List<MetaExpression> result = new ArrayList<>(leftValue);
        result.addAll(rightValue);

        return new ListExpression(result);
    }
}
