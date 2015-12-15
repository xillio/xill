package nl.xillio.xill.components.expressions;

import java.util.*;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.Robot;
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
	private InstructionFlow<MetaExpression> process(MetaExpression result, Debugger debugger){
		switch (result.getType()) {
			case ATOMIC:
				return atomicProcess(result, debugger);
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
	private InstructionFlow<MetaExpression> atomicProcess(MetaExpression input, Debugger debugger){
		if (input.isNull()) {
			return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
		}
		List<MetaExpression> boolValue = atomicHandler(input, debugger);
		List<MetaExpression> atomicResults = new ArrayList<>(1);

		if (boolValue.get(0).getBooleanValue()) {
			atomicResults.add(input);
			return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
		} else {
			return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
		}
	}

    /**
     * Filter process of an list value.
     * Perform filter function on all elements of the list (one layer deep).
     */
	@SuppressWarnings("unchecked")
	private InstructionFlow<MetaExpression> listProcess(MetaExpression input, Debugger debugger){
        List<MetaExpression> listInput = (List<MetaExpression>)input.getValue();
        List<MetaExpression> boolValues = listHandler(input, debugger);
        List<MetaExpression> listResults = new ArrayList<>();

		for (int i = 0; i < boolValues.size(); i++) {
			MetaExpression valueList = boolValues.get(i);
            valueList.registerReference();

			if (valueList.getBooleanValue()) {
                listResults.add(listInput.get(i));
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
	private InstructionFlow<MetaExpression> objectProcess(MetaExpression input, Debugger debugger){
		LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>();
		Set<Entry<String, MetaExpression>> boolValues = objectHandler(input, debugger).entrySet();

		for (Entry<String, MetaExpression> expression : boolValues) {
			if (expression.getValue().getBooleanValue()) {
				objectResults.put(expression.getKey(), expression.getValue());
			}
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
