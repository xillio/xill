package nl.xillio.xill.components.expressions;

import java.util.*;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

/**
 * Filter a collection of expressions using a function parameter
 */
public class FilterExpression implements Processable, FunctionParameterExpression {

	private final Processable argument;
	private FunctionDeclaration functionDeclaration;

	/**
	 * Create a new {@link FilterExpression}
	 * 
	 * @param argument
	 */
	public FilterExpression(final Processable argument) {
		this.argument = argument;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		List<MetaExpression> listResults = new ArrayList<>();
		LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>();

		// Call the function for the argument
		MetaExpression result = argument.process(debugger).get();

		switch (result.getType()) {
			case ATOMIC:
				// Process the one argument
				MetaExpression expressionValue = functionDeclaration.run(debugger, Arrays.asList(result)).get();
				if (expressionValue.getBooleanValue()) {
					listResults.add(result);
					return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
				} else {
					return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
				}
			case LIST:
				// Pass every argument
				for (MetaExpression expression : (List<MetaExpression>) result.getValue()) {
					MetaExpression value = functionDeclaration.run(debugger, Arrays.asList(expression)).get();
					if (value.getBooleanValue()) {
						listResults.add(expression);
					}
				}
				return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
			case OBJECT:
				// Pass every argument but with key
				for (Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) result.getValue())
						.entrySet()) {
					MetaExpression value = functionDeclaration.run(debugger,
							Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue()))
							.get();
					if (value.getBooleanValue()) {
						objectResults.put(expression.getKey(), expression.getValue());
					}
				}
				return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(objectResults));
			default:
				throw new NotImplementedException("This MetaExpression has not been defined yet.");
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
