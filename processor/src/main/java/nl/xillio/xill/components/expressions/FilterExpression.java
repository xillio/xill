package nl.xillio.xill.components.expressions;

import java.util.*;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

/**
 * Filter a collection of expressions using a function parameter
 *
 * @author Pieter Soels, Thomas Biesaart
 */
public class FilterExpression extends MapFilterHandler implements Processable, FunctionParameterExpression {
    private final Processable argument;

    /**
     * Create a new {@link FilterExpression}
     *
     * @param argument
     */
    public FilterExpression(final Processable argument) {
        this.argument = argument;
    }

    /**
     * Start of instructions for the filter expression.
     */
    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        // Call the function for the argument
        MetaExpression result = argument.process(debugger).get();

        try {
            result.registerReference();
            return process(result, debugger);
        } catch (ConcurrentModificationException e) {
            throw new RobotRuntimeException("You can not change the expression upon which you are iterating.", e);
        } finally {
            result.releaseReference();
        }
    }

    /**
     * Depending on type of MetaExpression (result) process individually.
     */
    private InstructionFlow<MetaExpression> process(MetaExpression result, Debugger debugger) {
        switch (result.getType()) {
            case ATOMIC:
                if (result.isNull()) {
                    return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
                }
                if (result.getMeta(MetaExpressionIterator.class) == null){
                    return atomicProcessNoIterator(result, debugger);
                } else {
                    return atomicProcessIterator(result, debugger);
                }
            case LIST:
                return listProcess(result, debugger);
            case OBJECT:
                return objectProcess(result, debugger);
            default:
                throw new NotImplementedException("This MetaExpression has not been defined yet.");
        }
    }

    /**
     * Filter process of an atomic value
     * Result empty list when input is null
     */
    private InstructionFlow<MetaExpression> atomicProcessNoIterator(MetaExpression input, Debugger debugger) {
        MetaExpression boolValue;
        List<MetaExpression> atomicResults = new ArrayList<>(1);
        if (functionDeclaration.getParametersSize() == 1){
            boolValue = functionDeclaration.run(debugger, Collections.singletonList(input)).get();
        } else if (functionDeclaration.getParametersSize() == 2){
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

    private InstructionFlow<MetaExpression> atomicProcessIterator(MetaExpression input, Debugger debugger) {
        MetaExpressionIterator iterator = input.getMeta(MetaExpressionIterator.class);
        MetaExpression boolValue;
        List<MetaExpression> atomicResults = new ArrayList<>(1);
        int i = -1;

        while (iterator.hasNext()) {
            MetaExpression value = iterator.next();
            value.registerReference();

            if (functionDeclaration.getParametersSize() == 1){
                boolValue = (functionDeclaration.run(debugger, Collections.singletonList(value)).get());
            } else if (functionDeclaration.getParametersSize() == 2){
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
    private InstructionFlow<MetaExpression> listProcess(MetaExpression input, Debugger debugger) {
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
    @SuppressWarnings("unchecked")
    private InstructionFlow<MetaExpression> objectProcess(MetaExpression input, Debugger debugger) {
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
	 * Set the function parameter
	 * 
	 * @param functionDeclaration
	 */
	@Override
	public void setFunction(final FunctionDeclaration functionDeclaration) {
		this.functionDeclaration = functionDeclaration;
	}

}
