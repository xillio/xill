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

	@SuppressWarnings("unchecked")
	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		// Call the function for all arguments
		MetaExpression result = argument.process(debugger).get();

		try {
			result.registerReference();
			return process(result, debugger);
		} finally {
			result.releaseReference();
		}
	}

	private InstructionFlow<MetaExpression> process(MetaExpression result, Debugger debugger){
		List<MetaExpression> listResults = new ArrayList<>();
		LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>();

		switch (result.getType()) {
			case ATOMIC:
				// Process the one argument
				listResults.add(functionDeclaration.run(debugger, Arrays.asList(result)).get());
				return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
			case LIST:
				// Pass every argument
				for (MetaExpression expression : (List<MetaExpression>) result.getValue()) {
					listResults.add(functionDeclaration.run(debugger, Arrays.asList(expression)).get());
				}
				return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
			case OBJECT:
				// Pass every argument but with key
				for (Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) result.getValue()).entrySet()) {
					objectResults.put(expression.getKey(), functionDeclaration.run(debugger, Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue())).get());
				}
				return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(objectResults));
			default:
				throw new NotImplementedException("This MetaExpression has not been implemented yet");
		}
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
