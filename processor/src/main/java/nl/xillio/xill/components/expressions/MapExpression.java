package nl.xillio.xill.components.expressions;

import java.util.*;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;
import nl.xillio.xill.components.instructions.Instruction;

/**
 * Call a function for every value in a collection
 */
public class MapExpression implements Processable, FunctionParameterExpression {

	private final Processable argument;
	private FunctionDeclaration functionDeclaration;

	/**
	 * Create a new {@link MapExpression}
	 * 
	 * @param argument
	 */
	public MapExpression(final Processable argument) {
		this.argument = argument;
	}

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

	private InstructionFlow<MetaExpression> process(MetaExpression result, Debugger debugger){
		switch (result.getType()) {
			case ATOMIC:
				// Process the one argument
				return atomicProcess(result, debugger);
			case LIST:
				// Pass every argument
				return listProcess(result, debugger);
			case OBJECT:
				// Pass every argument but with key
				return objectProcess(result, debugger);
			default:
				throw new NotImplementedException("This MetaExpression has not been implemented yet");
		}
	}

	private InstructionFlow<MetaExpression> atomicProcess(MetaExpression result, Debugger debugger){
		if (result.isNull()) {
			return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
		}
		List<MetaExpression> atomicResults = new ArrayList<>(1);
		atomicResults.add(functionDeclaration.run(debugger, Collections.singletonList(result)).get());
		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
	}

	@SuppressWarnings("unchecked")
	private InstructionFlow<MetaExpression> listProcess(MetaExpression result, Debugger debugger){
		List<MetaExpression> listResults = new ArrayList<>(result.getNumberValue().intValue());
		for (MetaExpression expression : (List<MetaExpression>) result.getValue()) {
			listResults.add(functionDeclaration.run(debugger, Collections.singletonList(expression)).get());
		}
		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
	}

	@SuppressWarnings("unchecked")
	private InstructionFlow<MetaExpression> objectProcess(MetaExpression result, Debugger debugger){
		LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>(result.getNumberValue().intValue());
		for (Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) result.getValue()).entrySet()) {
			objectResults.put(expression.getKey(), functionDeclaration.run(debugger,
					Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue())).get());
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
