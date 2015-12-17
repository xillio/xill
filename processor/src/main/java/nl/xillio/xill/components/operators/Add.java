package nl.xillio.xill.components.operators;

import nl.xillio.util.MathUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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

        leftValue.registerReference();
        rightValue.registerReference();

        try {
            //If you try to add a null value to anything it should give an error.
            if (leftValue.isNull() || rightValue.isNull()) {
                throw new RobotRuntimeException("An addition has been tried upon a null value");
            }

            //If you try to add a list to an atomic value it should give an error since this is unwanted behaviour.
            if (leftValue.getType() == ExpressionDataType.ATOMIC && rightValue.getType() == ExpressionDataType.LIST) {
                throw new RobotRuntimeException("You can not add a list to an atomic value.");
            }

            //If you try to add an object to an atomic value it should give an error since this is unwanted behaviour.
            if (leftValue.getType() == ExpressionDataType.ATOMIC && rightValue.getType() == ExpressionDataType.OBJECT) {
                throw new RobotRuntimeException("You can not add an object to an atomic value.");
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
            // If the left entry is a list and the right entry is an atomic value, Add the atomic value to the list.

            if (leftValue.getType() == ExpressionDataType.LIST && rightValue.getType() == ExpressionDataType.ATOMIC) {
                return InstructionFlow.doResume(
                        addToList((List<MetaExpression>) leftValue.getValue(), rightValue, debugger));
            }
            // If the left entry is an object and the right entry is an atomic value, Add the atomic value to the object.
            if (leftValue.getType() == ExpressionDataType.OBJECT && rightValue.getType() == ExpressionDataType.ATOMIC) {
                return InstructionFlow.doResume(
                        addToObject((LinkedHashMap<String, MetaExpression>) leftValue.getValue(), rightValue, debugger));
            }
            // If the left entry is a list and the right entry is an object,
            // then convert the list to an object and put it in front of the object.
            if (leftValue.getType() == ExpressionDataType.LIST && rightValue.getType() == ExpressionDataType.OBJECT) {
                return InstructionFlow.doResume(
                        processListObject((List<MetaExpression>) leftValue.getValue(),
                                (LinkedHashMap<String, MetaExpression>) rightValue.getValue(), debugger));
            }

            // If the left entry is an object and the right entry is a list,
            // then convert the list to an object and put it at the end of the object.
            if (leftValue.getType() == ExpressionDataType.OBJECT && rightValue.getType() == ExpressionDataType.LIST) {
                return InstructionFlow.doResume(
                        processObjectList((LinkedHashMap<String, MetaExpression>) leftValue.getValue(),
                                (List<MetaExpression>) rightValue.getValue(), debugger));
            }

            // The left and right entries are atomics so add them as numbers.
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
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>(leftValue);
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

    private static MetaExpression addToList(final List<MetaExpression> leftValue, final MetaExpression rightValue, final Debugger debugger) throws RobotRuntimeException {
        List<MetaExpression> result = new ArrayList<>(leftValue);
        result.add(rightValue);
        return ExpressionBuilderHelper.fromValue(result);
    }

    private static MetaExpression addToObject(final LinkedHashMap<String, MetaExpression> leftValue,final MetaExpression rightValue,final Debugger debugger) throws RobotRuntimeException {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>(leftValue);
        result.put(Integer.toString(leftValue.size()), rightValue);
        return ExpressionBuilderHelper.fromValue(result);
    }
}
