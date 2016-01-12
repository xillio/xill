package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.*;
import java.util.Map.Entry;

/**
 * Call a function for every value in a collection.
 *
 * @author Pieter Soels
 * @author Thomas Biesaart
 */
public class MapExpression extends MapFilterHandler {
    /**
     * Create a new {@link MapExpression}.
     *
     * @param argument the input for this expression
     */
    public MapExpression(final Processable argument) {
        super(argument);
    }

    /**
     * Map process of an atomic value
     * Result empty list when input is null
     */
    @Override
    protected InstructionFlow<MetaExpression> atomicProcessNoIterator(MetaExpression input, Debugger debugger) {
        List<MetaExpression> atomicResults = new ArrayList<>(1);
        if (functionDeclaration.getParametersSize() == 1) {
            atomicResults.add(functionDeclaration.run(debugger, Collections.singletonList(input)).get());
        } else if (functionDeclaration.getParametersSize() == 2) {
            atomicResults.add(functionDeclaration.run(debugger,
                    Arrays.asList(ExpressionBuilderHelper.fromValue(0), input)).get());
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }
        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
    }

    protected InstructionFlow<MetaExpression> atomicProcessIterator(MetaExpression input, Debugger debugger) {
        MetaExpressionIterator iterator = input.getMeta(MetaExpressionIterator.class);
        List<MetaExpression> atomicResults = new ArrayList<>(1);
        int i = -1;

        while (iterator.hasNext()) {
            MetaExpression value = iterator.next();
            value.registerReference();

            if (functionDeclaration.getParametersSize() == 1) {
                atomicResults.add(functionDeclaration.run(debugger, Collections.singletonList(value)).get());
            } else if (functionDeclaration.getParametersSize() == 2) {
                int keyValue = i++;
                atomicResults.add(functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(keyValue), value)).get());
            } else {
                throw new RobotRuntimeException("The given function does not accept one or two arguments.");
            }
        }
        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
    }

    /**
     * Map process of an list value.
     * Perform function on all elements of the list (one layer deep).
     */
    @SuppressWarnings("unchecked")
    protected InstructionFlow<MetaExpression> listProcess(MetaExpression result, Debugger debugger) {
        List<MetaExpression> listResults = new ArrayList<>(result.getNumberValue().intValue());

        if (functionDeclaration.getParametersSize() == 1) {
            for (MetaExpression expression : (List<MetaExpression>) result.getValue()) {
                InstructionFlow<MetaExpression> functionResult = functionDeclaration.run(debugger, Collections.singletonList(expression));
                MetaExpression resultValue = functionResult.get();
                listResults.add(resultValue);
            }
        } else if (functionDeclaration.getParametersSize() == 2) {
            List<MetaExpression> expressions = (List<MetaExpression>) result.getValue();
            for (int i = 0; i < expressions.size(); i++) {
                listResults.add(functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(i), expressions.get(i))).get());
            }
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }

        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
    }

    /**
     * Map process of an object value.
     * Perform function on all elements in object (one layer deep).
     */
    @SuppressWarnings("unchecked")
    protected InstructionFlow<MetaExpression> objectProcess(MetaExpression result, Debugger debugger) {
        LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>(result.getNumberValue().intValue());

        if (functionDeclaration.getParametersSize() == 1) {
            for (Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) result.getValue()).entrySet()) {
                objectResults.put(expression.getKey(), functionDeclaration.run(debugger, Collections.singletonList(expression.getValue())).get());
            }
        } else if (functionDeclaration.getParametersSize() == 2) {
            for (Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) result.getValue()).entrySet()) {
                objectResults.put(expression.getKey(), functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue())).get());
            }
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }

        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(objectResults));
    }

    @Override
    public Collection<Processable> getChildren() {
        return null;
    }

}
