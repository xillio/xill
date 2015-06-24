package nl.xillio.xill.components.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;
import nl.xillio.xill.components.operators.Assign;

/**
 * This class represents a call of a custom function
 */
public class FunctionCall implements Processable {

	private FunctionDeclaration function;
	private List<Assign> arguments;

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		return function.run(debugger, arguments);
	}

	/**
	 * @param function
	 *        the function to set
	 */
	public void setFunction(final FunctionDeclaration function) {
		this.function = function;
	}

	/**
	 * @param arguments
	 *        the arguments to set
	 */
	public void setArguments(final List<Assign> arguments) {
		this.arguments = arguments;
	}

	@Override
	public Collection<Processable> getChildren() {
		List<Processable> children = new ArrayList<>(arguments);
		children.add(function);
		return children;
	}
}
