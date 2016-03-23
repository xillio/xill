package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.*;
import java.util.Map.Entry;

/**
 * Filter a collection of expressions using a function parameter
 *
 * @author Pieter Soels, Thomas Biesaart
 */
public class FilterExpression extends MapFilterHandler {
    /**
     * Create a new {@link FilterExpression}-object.
     *
     * @param argument    the argument passed when a new FilterExpression-object is created.
     */
    public FilterExpression(final Processable argument) {
        super(argument);
    }

    /**
     * Filter process of an atomic value
     * Result empty list when input is null
     */
    @Override
    protected InstructionFlow<MetaExpression> atomicProcessNoIterator(MetaExpression input, Debugger debugger) {
        MetaExpression boolValue;
        List<MetaExpression> atomicResults = new ArrayList<>(1);
        if (functionDeclaration.getParametersSize() == 1) {
            boolValue = functionDeclaration.run(debugger, Collections.singletonList(input)).get();
        } else if (functionDeclaration.getParametersSize() == 2) {
            boolValue = functionDeclaration.run(debugger,
                    Arrays.asList(ExpressionBuilderHelper.fromValue(0), input)).get();
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }

        boolValue.registerReference();

        if (boolValue.getBooleanValue()) {
            atomicResults.add(input);
        }

        boolValue.releaseReference();
        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
    }

    @Override
    protected InstructionFlow<MetaExpression> atomicProcessIterator(MetaExpression input, Debugger debugger) {
        MetaExpressionIterator iterator = input.getMeta(MetaExpressionIterator.class);
        MetaExpression boolValue;
        List<MetaExpression> atomicResults = new ArrayList<>(1);
        int i = -1;

        while (iterator.hasNext()) {
            if (debugger.shouldStop()) {
                break;
            }
            MetaExpression value = iterator.next();
            value.registerReference();

            if (functionDeclaration.getParametersSize() == 1) {
                boolValue = (functionDeclaration.run(debugger, Collections.singletonList(value)).get());
            } else if (functionDeclaration.getParametersSize() == 2) {
                int keyValue = i++;
                boolValue = (functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(keyValue), value)).get());
            } else {
                throw new RobotRuntimeException("The given function does not accept one or two arguments.");
            }
            boolValue.registerReference();

            if (boolValue.getBooleanValue()) {
                atomicResults.add(input);
            }

            boolValue.releaseReference();
        }
        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
    }

    /**
     * Filter process of an list value.
     * Perform filter function on all elements of the list (one layer deep).
     */
    @SuppressWarnings("unchecked")
    @Override
    protected InstructionFlow<MetaExpression> listProcess(MetaExpression input, Debugger debugger) {
        List<MetaExpression> listResults = new ArrayList<>();
        List<MetaExpression> expressions = (List<MetaExpression>) input.getValue();

        for (int i = 0; i < expressions.size(); i++) {
            MetaExpression valueList;

            if (functionDeclaration.getParametersSize() == 1) {
                valueList = functionDeclaration.run(debugger, Collections.singletonList(expressions.get(i))).get();
                valueList.registerReference();
            } else if (functionDeclaration.getParametersSize() == 2) {
                valueList = functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(i), expressions.get(i))).get();
                valueList.registerReference();
            } else {
                throw new RobotRuntimeException("The given function does not accept one or two arguments.");
            }

            if (valueList.getBooleanValue()) {
                listResults.add(expressions.get(i));
            }
            valueList.releaseReference();
        }
        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));

    }

    /**
     * Filter process of an object value.
     * Perform filter on all elements in object (one layer deep).
     */
    @Override
    @SuppressWarnings("unchecked")
    protected InstructionFlow<MetaExpression> objectProcess(MetaExpression input, Debugger debugger) {
        LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>();
        Set<Entry<String, MetaExpression>> expressions = ((Map<String, MetaExpression>) input.getValue()).entrySet();

        for (Entry<String, MetaExpression> expression : expressions) {
            MetaExpression valueObject;

            if (functionDeclaration.getParametersSize() == 1) {
                valueObject = functionDeclaration.run(debugger, Collections.singletonList(expression.getValue())).get();
                valueObject.registerReference();
            } else if (functionDeclaration.getParametersSize() == 2) {
                valueObject = functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue())).get();
                valueObject.registerReference();
            } else {
                throw new RobotRuntimeException("The given function does not accept one or two arguments.");
            }
            if (valueObject.getBooleanValue()) {
                objectResults.put(expression.getKey(), expression.getValue());
            }
            valueObject.releaseReference();
        }
        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(objectResults));
    }

    @Override
    public Collection<Processable> getChildren() {
        return null;
    }

    /**
     * Set the function parameter.
     *
     * @param functionDeclaration    the value to which the function declaration needs to be set.
     */
    @Override
    public void setFunction(final FunctionDeclaration functionDeclaration) {
        super.functionDeclaration = functionDeclaration;
    }

}
