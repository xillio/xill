package nl.xillio.xill.components.operators;

import nl.xillio.util.MathUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        leftValue.registerReference();
        rightValue.registerReference();

        try {
            if (leftValue.isNull() || rightValue.isNull()) {
                throw new RobotRuntimeException("An addition has been tried upon a null value");
            }

            // If both entries are a list, then add them as such
            if (leftValue.getType() == rightValue.getType() && leftValue.getType() == ExpressionDataType.LIST) {
                return InstructionFlow.doResume(
                        processLists((List<MetaExpression>) leftValue.getValue(),
                                (List<MetaExpression>) rightValue.getValue(), debugger));
            }
            // If both entries are an object, then add them as such
            if (leftValue.getType() == rightValue.getType() && leftValue.getType() == ExpressionDataType.OBJECT) {
                return InstructionFlow.doResume(
                        processObjects((LinkedHashMap<String, MetaExpression>) leftValue.getValue(),
                                (LinkedHashMap<String, MetaExpression>) rightValue.getValue(), debugger));
            }

            if (leftValue.getType() == ExpressionDataType.LIST && rightValue.getType() == ExpressionDataType.OBJECT){
                return InstructionFlow.doResume(
                        processListObject((List<MetaExpression>) leftValue.getValue(),
                                (LinkedHashMap<String, MetaExpression>) rightValue.getValue(), debugger));
            }

            if (leftValue.getType() == ExpressionDataType.OBJECT && rightValue.getType() == ExpressionDataType.LIST){
                return InstructionFlow.doResume(
                        processObjectList((LinkedHashMap<String, MetaExpression>) leftValue.getValue(),
                                (List<MetaExpression>) rightValue.getValue(), debugger));
            }

            return super.process(leftValue, rightValue);
        } finally {
            rightValue.releaseReference();
            leftValue.releaseReference();
        }

    }

    private static MetaExpression processLists(final List<MetaExpression> leftValue, final List<MetaExpression> rightValue, final Debugger debugger) throws RobotRuntimeException {
        List<MetaExpression> result = new ArrayList<>(leftValue);
        result.addAll(rightValue);

        return new ListExpression(result);
    }

    private static MetaExpression processObjects(final LinkedHashMap<String, MetaExpression> leftValue, final LinkedHashMap<String, MetaExpression> rightValue, final Debugger debugger) throws RobotRuntimeException {
        LinkedHashMap<String, MetaExpression> result = leftValue;
        result.putAll(rightValue);

        return ExpressionBuilderHelper.fromValue(result);
    }

    private static MetaExpression processListObject(final List<MetaExpression> leftValue, final LinkedHashMap<String, MetaExpression> rightValue, final Debugger debugger) throws RobotRuntimeException {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
        for (int i = 0; i < leftValue.size(); i++) {
            result.put(Integer.toString(i), leftValue.get(i));
        }
        result.putAll(rightValue);

        return ExpressionBuilderHelper.fromValue(result);
    }

    private static MetaExpression processObjectList(final LinkedHashMap<String, MetaExpression> leftValue, final List<MetaExpression> rightValue, final Debugger debugger) throws RobotRuntimeException {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>(leftValue);
        for (int i = leftValue.size(); i < leftValue.size() + rightValue.size(); i++) {
            result.put(Integer.toString(i), rightValue.get(i - leftValue.size()));
        }

        return ExpressionBuilderHelper.fromValue(result);
    }
}
