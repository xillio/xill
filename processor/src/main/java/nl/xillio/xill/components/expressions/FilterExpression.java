package nl.xillio.xill.components.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

/**
 * Filter a collection of expressions using a function parameter
 */
public class FilterExpression implements Processable, FunctionParameterExpression {

	private final List<Processable> arguments;
	private FunctionDeclaration functionDeclaration;

	/**
	 * Create a new {@link FilterExpression}
	 * 
	 * @param arguments
	 */
	public FilterExpression(final List<Processable> arguments) {
		this.arguments = arguments;
	}

	@SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
	// This RobotRuntimeException will not be addressed as it triggers editor specific behaviour.
	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		List<MetaExpression> results = new ArrayList<>();

		// Call the function for all arguments
		for (Processable argument : arguments) {
			MetaExpression result = argument.process(debugger).get();

			switch (result.getType()) {
				case ATOMIC:
					// Process the one argument
					MetaExpression expressionValue = functionDeclaration.run(debugger, Arrays.asList(result)).get();
					if (expressionValue.getBooleanValue()) {
						results.add(result);
					}
					break;
				case LIST:
					// Pass every argument
					for (MetaExpression expression : (List<MetaExpression>) result.getValue()) {
						MetaExpression value = functionDeclaration.run(debugger, Arrays.asList(expression)).get();
						if (value.getBooleanValue()) {
							results.add(expression);
						}
					}
					break;
				case OBJECT:
					// Pass every argument but with key
					for (Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) result.getValue())
						.entrySet()) {
						MetaExpression value = functionDeclaration.run(debugger,
							Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue()))
							.get();
						if (value.getBooleanValue()) {
							results.add(expression.getValue());
						}
					}
					break;
				default:
					break;

			}
		}

		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(results));
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
