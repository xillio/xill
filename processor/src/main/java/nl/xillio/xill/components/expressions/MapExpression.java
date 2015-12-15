package nl.xillio.xill.components.expressions;

import java.util.*;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;
import nl.xillio.xill.components.instructions.Instruction;

/**
 * Call a function for every value in a collection
 *
 * @author Pieter Soels, Thomas Biesaart
 */
public class MapExpression extends MapFilterHandler implements Processable, FunctionParameterExpression {
	private final Processable argument;

	/**
	 * Create a new {@link MapExpression}
	 * 
	 * @param argument
	 */
	public MapExpression(final Processable argument) {
		this.argument = argument;
	}

    /**
     * Start of instructions for the map expression.
     */
	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) {
		// Call the function for all arguments
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
				throw new NotImplementedException("This MetaExpression has not been implemented yet");
		}
	}

    /**
     * Map process of an atomic value
     * Result empty list when input is null
     */
	private InstructionFlow<MetaExpression> atomicProcess(MetaExpression input, Debugger debugger){
		if (input.isNull()) {
			return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
		}
		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicHandler(input, debugger)));
	}

    /**
     * Map process of an list value.
     * Perform function on all elements of the list (one layer deep).
     */
	private InstructionFlow<MetaExpression> listProcess(MetaExpression input, Debugger debugger){
		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listHandler(input, debugger)));
	}

    /**
     * Map process of an object value.
     * Perform function on all elements in object (one layer deep).
     */
	private InstructionFlow<MetaExpression> objectProcess(MetaExpression result, Debugger debugger){
		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(objectHandler(result, debugger)));
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
