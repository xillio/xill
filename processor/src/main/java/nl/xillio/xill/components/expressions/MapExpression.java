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
//Implemented key-value argument for map
	private InstructionFlow<MetaExpression> atomicProcess(MetaExpression result, Debugger debugger){
		if (result.isNull()) {
			return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
		}
		List<MetaExpression> atomicResults = new ArrayList<>(1);

		if (functionDeclaration.getParametersSize() == 1){
			atomicResults.add(functionDeclaration.run(debugger, Collections.singletonList(result)).get());
		} else if (functionDeclaration.getParametersSize() == 2){
			atomicResults.add(functionDeclaration.run(debugger,
					Arrays.asList(ExpressionBuilderHelper.fromValue(0), result)).get());
		} else {
			throw new RobotRuntimeException("The given function does not accept only one argument.");
		}

		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
	}

	@SuppressWarnings("unchecked")
	private InstructionFlow<MetaExpression> listProcess(MetaExpression result, Debugger debugger){
		List<MetaExpression> listResults = new ArrayList<>(result.getNumberValue().intValue());

		if (functionDeclaration.getParametersSize() == 1) {
			for (MetaExpression expression : (List<MetaExpression>) result.getValue()) {
				listResults.add(functionDeclaration.run(debugger, Collections.singletonList(expression)).get());
			}
		} else if (functionDeclaration.getParametersSize() == 2) {
			List<MetaExpression> expressions = (List<MetaExpression>)result.getValue();
			for (int i = 0; i < expressions.size() ; i++) {
				listResults.add(functionDeclaration.run(debugger,
						Arrays.asList(ExpressionBuilderHelper.fromValue(i), expressions.get(i))).get());
			}
		} else {
			throw new RobotRuntimeException("The given function does not accept one or two arguments.");
		}

		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
	}

	@SuppressWarnings("unchecked")
	private InstructionFlow<MetaExpression> objectProcess(MetaExpression result, Debugger debugger){
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
