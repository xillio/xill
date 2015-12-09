package nl.xillio.xill.components.operators;


import nl.xillio.util.MathUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class represents a binary operation on numbers that results in a boolean expression.
 */
abstract class BinaryNumberComparisonOperator implements Processable {

    protected final Processable left;
    protected final Processable right;

    BinaryNumberComparisonOperator(Processable left, Processable right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public InstructionFlow<MetaExpression> process(Debugger debugger) throws RobotRuntimeException {
        MetaExpression leftValue = left.process(debugger).get();
        MetaExpression rightValue = right.process(debugger).get();

        return process(leftValue, rightValue);
    }

    protected InstructionFlow<MetaExpression> process(MetaExpression left, MetaExpression right) {
        left.registerReference();
        right.registerReference();

        Number leftValue = left.getNumberValue();
        Number rightValue = right.getNumberValue();
        int comparisonResult = MathUtils.compare(leftValue, rightValue);
        MetaExpression result = ExpressionBuilder.fromValue(translate(comparisonResult));

        left.releaseReference();
        right.releaseReference();
        return InstructionFlow.doResume(result);
    }

    /**
     * Translate a comparison to a boolean result.
     *
     * @param comparisonResult the value {@code 0} if {@code x == y};
     *                         a value less than {@code 0} if {@code x < y}; and
     *                         a value greater than {@code 0} if {@code x > y}
     * @return the result for the operation
     */
    protected abstract boolean translate(int comparisonResult);


    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(left, right);
    }
}
