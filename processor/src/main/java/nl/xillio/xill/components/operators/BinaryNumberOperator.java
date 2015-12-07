package nl.xillio.xill.components.operators;


import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiFunction;

/**
 * This class represents an abstract operation that involved two number operands and results in a new number.
 *
 * @author Thomas Biesaart
 */
abstract class BinaryNumberOperator implements Processable {

    protected final Processable left;
    protected final Processable right;
    private final BiFunction<Number, Number, Number> operator;

    protected BinaryNumberOperator(Processable left, Processable right, BiFunction<Number, Number, Number> operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public InstructionFlow<MetaExpression> process(Debugger debugger) throws RobotRuntimeException {
        MetaExpression leftValue = left.process(debugger).get();
        MetaExpression rightValue = right.process(debugger).get();

        return process(leftValue, rightValue);
    }

    protected InstructionFlow<MetaExpression> process(MetaExpression leftValue, MetaExpression rightValue) {
        leftValue.registerReference();
        rightValue.registerReference();
        Number result = operator.apply(leftValue.getNumberValue(), rightValue.getNumberValue());
        leftValue.releaseReference();
        rightValue.releaseReference();

        return InstructionFlow.doResume(ExpressionBuilder.fromValue(result));
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(left, right);
    }


}
